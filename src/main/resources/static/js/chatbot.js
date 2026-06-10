let currentConversationId = null;
let chatSessionId = sessionStorage.getItem("shoestore_chat_session_id");
if (!chatSessionId) {
    chatSessionId = 'sess_' + Math.random().toString(36).substring(2, 15) + '_' + Date.now();
    sessionStorage.setItem("shoestore_chat_session_id", chatSessionId);
}

document.addEventListener("DOMContentLoaded", () => {
    const trigger = document.getElementById("ss-chatbot-trigger");
    const container = document.getElementById("ss-chatbot-container");
    const closeBtn = document.getElementById("ss-chatbot-close");
    const historyBtn = document.getElementById("ss-chatbot-history-btn");
    const closeHistoryBtn = document.getElementById("ss-chatbot-close-history");
    const input = document.getElementById("ss-chatbot-input");
    const sendBtn = document.getElementById("ss-chatbot-send");
    const messagesContainer = document.getElementById("ss-chatbot-messages");
    const historyPanel = document.getElementById("ss-chatbot-history-panel");
    const historyList = document.getElementById("ss-chatbot-history-list");

    // Check if global auth variable exists, fallback to false if not set
    const isAuthenticated = typeof IS_AUTHENTICATED !== 'undefined' ? IS_AUTHENTICATED : false;

    // Resize capability (mouse & touch)
    if (container) {
        const resizerL = document.createElement("div");
        resizerL.className = "ss-chatbot-resizer ss-chatbot-resizer-l";
        const resizerT = document.createElement("div");
        resizerT.className = "ss-chatbot-resizer ss-chatbot-resizer-t";
        const resizerTL = document.createElement("div");
        resizerTL.className = "ss-chatbot-resizer ss-chatbot-resizer-tl";

        container.appendChild(resizerL);
        container.appendChild(resizerT);
        container.appendChild(resizerTL);

        let startWidth = 0;
        let startHeight = 0;
        let startX = 0;
        let startY = 0;
        let isResizing = false;
        let currentHandle = null;

        const initResize = (e, handle) => {
            isResizing = true;
            currentHandle = handle;
            startX = e.clientX;
            startY = e.clientY;
            startWidth = parseInt(document.defaultView.getComputedStyle(container).width, 10);
            startHeight = parseInt(document.defaultView.getComputedStyle(container).height, 10);

            document.body.classList.add("ss-chatbot-resizing");
            if (handle === 'l') document.body.style.cursor = 'ew-resize';
            if (handle === 't') document.body.style.cursor = 'ns-resize';
            if (handle === 'tl') document.body.style.cursor = 'nwse-resize';

            container.style.transition = 'none';

            document.addEventListener("mousemove", resize);
            document.addEventListener("mouseup", stopResize);
            e.preventDefault();
        };

        const resize = (e) => {
            if (!isResizing) return;

            let newWidth = startWidth;
            let newHeight = startHeight;

            if (currentHandle === 'l' || currentHandle === 'tl') {
                const dx = e.clientX - startX;
                newWidth = startWidth - dx;
            }
            if (currentHandle === 't' || currentHandle === 'tl') {
                const dy = e.clientY - startY;
                newHeight = startHeight - dy;
            }

            const minWidth = 320;
            const maxWidth = window.innerWidth * 0.95;
            const minHeight = 400;
            const maxHeight = window.innerHeight * 0.85;

            if (newWidth >= minWidth && newWidth <= maxWidth) {
                container.style.width = newWidth + 'px';
            }
            if (newHeight >= minHeight && newHeight <= maxHeight) {
                container.style.height = newHeight + 'px';
            }
        };

        const stopResize = () => {
            isResizing = false;
            document.body.classList.remove("ss-chatbot-resizing");
            document.body.style.cursor = '';
            container.style.transition = '';

            document.removeEventListener("mousemove", resize);
            document.removeEventListener("mouseup", stopResize);
        };

        const initResizeTouch = (e, handle) => {
            if (e.touches.length !== 1) return;
            const touch = e.touches[0];
            isResizing = true;
            currentHandle = handle;
            startX = touch.clientX;
            startY = touch.clientY;
            startWidth = parseInt(document.defaultView.getComputedStyle(container).width, 10);
            startHeight = parseInt(document.defaultView.getComputedStyle(container).height, 10);
            container.style.transition = 'none';

            document.addEventListener("touchmove", resizeTouch, { passive: false });
            document.addEventListener("touchend", stopResizeTouch);
        };

        const resizeTouch = (e) => {
            if (!isResizing || e.touches.length !== 1) return;
            const touch = e.touches[0];
            let newWidth = startWidth;
            let newHeight = startHeight;

            if (currentHandle === 'l' || currentHandle === 'tl') {
                const dx = touch.clientX - startX;
                newWidth = startWidth - dx;
            }
            if (currentHandle === 't' || currentHandle === 'tl') {
                const dy = touch.clientY - startY;
                newHeight = startHeight - dy;
            }

            const minWidth = 320;
            const maxWidth = window.innerWidth * 0.95;
            const minHeight = 400;
            const maxHeight = window.innerHeight * 0.85;

            if (newWidth >= minWidth && newWidth <= maxWidth) {
                container.style.width = newWidth + 'px';
            }
            if (newHeight >= minHeight && newHeight <= maxHeight) {
                container.style.height = newHeight + 'px';
            }
            e.preventDefault();
        };

        const stopResizeTouch = () => {
            isResizing = false;
            container.style.transition = '';
            document.removeEventListener("touchmove", resizeTouch);
            document.removeEventListener("touchend", stopResizeTouch);
        };

        resizerL.addEventListener("mousedown", (e) => initResize(e, 'l'));
        resizerT.addEventListener("mousedown", (e) => initResize(e, 't'));
        resizerTL.addEventListener("mousedown", (e) => initResize(e, 'tl'));

        resizerL.addEventListener("touchstart", (e) => initResizeTouch(e, 'l'), { passive: true });
        resizerT.addEventListener("touchstart", (e) => initResizeTouch(e, 't'), { passive: true });
        resizerTL.addEventListener("touchstart", (e) => initResizeTouch(e, 'tl'), { passive: true });
    }

    // Expand button toggle
    const expandBtn = document.getElementById("ss-chatbot-expand");
    if (expandBtn) {
        expandBtn.addEventListener("click", () => {
            container.classList.toggle("expanded");
            const icon = expandBtn.querySelector("i");
            if (container.classList.contains("expanded")) {
                container.style.width = "";
                container.style.height = "";
                if (icon) {
                    icon.className = "bi bi-arrows-angle-contract";
                }
            } else {
                container.style.width = "";
                container.style.height = "";
                if (icon) {
                    icon.className = "bi bi-arrows-angle-expand";
                }
            }
        });
    }

    // Toggle chatbot widget
    if (trigger) {
        trigger.addEventListener("click", () => {
            container.classList.toggle("active");
            if (container.classList.contains("active")) {
                input.focus();
                // If opening and messages container is empty, load initial state
                if (messagesContainer.children.length === 0) {
                    initChat();
                }
            }
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            container.classList.remove("active");
        });
    }

    // Toggle history panel (only for authenticated users)
    if (historyBtn && isAuthenticated) {
        historyBtn.addEventListener("click", () => {
            historyPanel.classList.add("active");
            loadConversations();
        });
    }

    if (closeHistoryBtn) {
        closeHistoryBtn.addEventListener("click", () => {
            historyPanel.classList.remove("active");
        });
    }

    // Send message on click or Enter key
    if (sendBtn) {
        sendBtn.addEventListener("click", sendChatbotMessage);
    }

    if (input) {
        input.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                sendChatbotMessage();
            }
        });
    }

    // Event delegation for quick reply buttons click
    if (messagesContainer) {
        messagesContainer.addEventListener("click", (e) => {
            const replyBtn = e.target.closest(".ss-chatbot-quick-reply");
            if (replyBtn) {
                if (input.disabled) return;
                const msg = replyBtn.getAttribute("data-msg");
                if (msg) {
                    sendChatbotMessage(msg);
                }
            }
        });
    }

    // Initialize chat window
    function initChat() {
        messagesContainer.innerHTML = "";

        // Add welcome message
        appendMessage("assistant", "Chào mừng bạn đến với ShoeStore! Mình là trợ lý ảo hỗ trợ bạn chọn size, tìm sản phẩm và giải đáp các chính sách của cửa hàng. Bạn cần giúp gì ạ?");

        if (!isAuthenticated) {
            // Guest mode: load from localStorage if present
            const guestChat = localStorage.getItem("shoestore_guest_chat");
            if (guestChat) {
                try {
                    const parsed = JSON.parse(guestChat);
                    if (Array.isArray(parsed)) {
                        parsed.forEach(msg => {
                            appendMessage(msg.role, msg.content);
                        });
                    }
                } catch (e) {
                    console.error("Failed to parse guest chat history", e);
                }
            }
            // Append quick replies suggestions
            appendQuickReplies();
        } else {
            // Authenticated user mode: check if there is an active conversation ID in sessionStorage
            const activeConvId = sessionStorage.getItem("shoestore_active_conv_id");
            if (activeConvId) {
                loadConversationMessages(activeConvId);
            } else {
                appendQuickReplies();
            }
        }
    }

    // Append quick replies suggestions
    function appendQuickReplies() {
        // Only append if there isn't one already
        if (messagesContainer.querySelector(".ss-chatbot-quick-replies")) return;

        const quickRepliesDiv = document.createElement("div");
        quickRepliesDiv.className = "ss-chatbot-quick-replies";

        const replies = [
            { text: "🔥 Giày hot?", msg: "Hiện tại có những giày nào hot" },
            { text: "👟 Giày Sneaker", msg: "giày sneaker có những mẫu nào" },
            { text: "📦 Đổi trả hàng", msg: "Chính sách đổi trả hàng như thế nào" },
            { text: "📏 Chọn size giày", msg: "Cách chọn size giày như thế nào" },
            { text: "🔑 Quên mật khẩu", msg: "Tôi bị quên mật khẩu phải làm sao" },
            { text: "🛒 Đơn hàng", msg: "Đơn hàng của tôi ở đâu" }
        ];

        replies.forEach(reply => {
            const btn = document.createElement("button");
            btn.className = "ss-chatbot-quick-reply";
            btn.setAttribute("data-msg", reply.msg);
            btn.textContent = reply.text;
            quickRepliesDiv.appendChild(btn);
        });

        messagesContainer.appendChild(quickRepliesDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    // Append message to UI
    function appendMessage(role, content) {
        const messageDiv = document.createElement("div");
        messageDiv.className = `ss-chatbot-message ss-chatbot-message-${role === "USER" || role === "user" ? "user" : "bot"}`;
 
        // Escape HTML
        let escaped = content
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
 
        // Parse markdown styles
        let formatted = escaped
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank">$1</a>');
 
        // Parse lists and newlines
        let lines = formatted.split('\n');
        let htmlLines = [];
        let inList = false;
        let inNumList = false;
 
        lines.forEach(line => {
            let trimmed = line.trim();
            
            // Unordered list (* or -)
            if (trimmed.startsWith('* ') || trimmed.startsWith('- ')) {
                if (!inList) {
                    if (inNumList) {
                        htmlLines.push('</ol>');
                        inNumList = false;
                    }
                    htmlLines.push('<ul class="ps-3 mb-2">');
                    inList = true;
                }
                htmlLines.push(`<li>${trimmed.substring(2)}</li>`);
            }
            // Ordered list (1. 2. etc)
            else if (/^\d+\.\s+/.test(trimmed)) {
                let match = trimmed.match(/^(\d+)\.\s+(.*)/);
                if (!inNumList) {
                    if (inList) {
                        htmlLines.push('</ul>');
                        inList = false;
                    }
                    htmlLines.push('<ol class="ps-3 mb-2">');
                    inNumList = true;
                }
                htmlLines.push(`<li>${match[2]}</li>`);
            }
            // Regular text paragraph
            else {
                if (inList) {
                    htmlLines.push('</ul>');
                    inList = false;
                }
                if (inNumList) {
                    htmlLines.push('</ol>');
                    inNumList = false;
                }
                if (trimmed.length > 0) {
                    htmlLines.push(`<p class="mb-2">${trimmed}</p>`);
                } else {
                    htmlLines.push('<div class="py-1"></div>');
                }
            }
        });
 
        if (inList) htmlLines.push('</ul>');
        if (inNumList) htmlLines.push('</ol>');
 
        messageDiv.innerHTML = htmlLines.join('');
        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    // Show/hide loading indicator
    function showLoading() {
        const loadingDiv = document.createElement("div");
        loadingDiv.id = "ss-chatbot-loading-indicator";
        loadingDiv.className = "ss-chatbot-loading";
        loadingDiv.innerHTML = `
            <span>Đang kiểm tra</span>
            <div class="ss-chatbot-loading-dots">
                <span></span>
                <span></span>
                <span></span>
            </div>
        `;
        messagesContainer.appendChild(loadingDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    function removeLoading() {
        const loadingDiv = document.getElementById("ss-chatbot-loading-indicator");
        if (loadingDiv) {
            loadingDiv.remove();
        }
    }

    // Send message to controller
    async function sendChatbotMessage(forcedText) {
        const text = (typeof forcedText === 'string' ? forcedText : input.value).trim();
        if (!text) return;

        // Remove existing quick replies suggestions
        const existingReplies = messagesContainer.querySelector(".ss-chatbot-quick-replies");
        if (existingReplies) {
            existingReplies.remove();
        }

        // Limiting empty or too short message
        input.value = "";

        // Disable inputs
        input.disabled = true;
        if (sendBtn) sendBtn.disabled = true;

        // Append user message to UI
        appendMessage("user", text);

        // Save guest chat to localStorage
        if (!isAuthenticated) {
            saveGuestMessage("user", text);
        }

        // Show loading
        showLoading();

        try {
            const response = await fetch("/api/chatbot", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    message: text,
                    conversationId: currentConversationId,
                    sessionId: chatSessionId
                })
            });

            removeLoading();

            if (response.ok) {
                const data = await response.json();

                // Set current conversation ID if it's the first message of a logged in user session
                if (isAuthenticated && data.conversationId) {
                    currentConversationId = data.conversationId;
                    sessionStorage.setItem("shoestore_active_conv_id", data.conversationId);
                }

                appendMessage("assistant", data.answer);

                if (!isAuthenticated) {
                    saveGuestMessage("assistant", data.answer);
                }
            } else {
                appendMessage("assistant", "Dạ, hiện tại hệ thống phản hồi đang bận. Bạn vui lòng thử lại sau giây lát nhé.");
            }
        } catch (error) {
            console.error("Chat error:", error);
            removeLoading();
            appendMessage("assistant", "Xin lỗi, đã xảy ra lỗi kết nối mạng. Bạn vui lòng kiểm tra lại kết nối và gửi lại câu hỏi giúp mình.");
        } finally {
            // Re-enable inputs
            input.disabled = false;
            sendBtn.disabled = false;
            input.focus();
        }
    }

    // Save message to localStorage for guest
    function saveGuestMessage(role, content) {
        let guestChat = localStorage.getItem("shoestore_guest_chat");
        let parsed = [];
        if (guestChat) {
            try {
                parsed = JSON.parse(guestChat);
            } catch (e) {
                parsed = [];
            }
        }
        parsed.push({ role, content });
        // Limit guest history length in localStorage to 30 messages
        if (parsed.length > 30) {
            parsed.shift();
        }
        localStorage.setItem("shoestore_guest_chat", JSON.stringify(parsed));
    }

    // Load list of conversations (logged in user only)
    async function loadConversations() {
        historyList.innerHTML = `<div class="text-center py-4 text-muted small"><div class="spinner-border spinner-border-sm text-secondary me-2"></div>Đang tải lịch sử...</div>`;

        try {
            const response = await fetch("/api/chatbot/conversations");
            if (response.ok) {
                const data = await response.json();
                historyList.innerHTML = "";

                // Add "Create New Conversation" button at the top of history list
                const newChatBtn = document.createElement("button");
                newChatBtn.className = "btn btn-outline-primary btn-sm w-100 mb-3 py-2 fw-bold";
                newChatBtn.innerHTML = "<i class='bi bi-plus-lg me-1'></i>Tạo cuộc hội thoại mới";
                newChatBtn.style.borderRadius = "20px";
                newChatBtn.style.fontSize = "0.75rem";
                newChatBtn.addEventListener("click", () => {
                    currentConversationId = null;
                    sessionStorage.removeItem("shoestore_active_conv_id");
                    historyPanel.classList.remove("active");
                    initChat();
                });
                historyList.appendChild(newChatBtn);

                if (data.length === 0) {
                    const noHistoryDiv = document.createElement("div");
                    noHistoryDiv.className = "text-center py-4 text-muted small";
                    noHistoryDiv.textContent = "Chưa có lịch sử cuộc trò chuyện nào.";
                    historyList.appendChild(noHistoryDiv);
                    return;
                }

                data.forEach(item => {
                    const date = new Date(item.updatedAt);
                    const dateStr = date.toLocaleDateString("vi-VN") + " " + date.toLocaleTimeString("vi-VN", { hour: '2-digit', minute: '2-digit' });

                    const itemDiv = document.createElement("div");
                    itemDiv.className = "ss-chatbot-history-item";

                    itemDiv.innerHTML = `
                        <div class="ss-chatbot-history-item-info">
                            <h6 class="ss-chatbot-history-item-title">${item.title}</h6>
                            <span class="ss-chatbot-history-item-date">${dateStr}</span>
                        </div>
                        <button class="ss-chatbot-history-delete-btn" data-id="${item.id}" title="Xóa hội thoại">
                            <i class="bi bi-trash"></i>
                        </button>
                    `;

                    // Click info loads conversation
                    itemDiv.querySelector(".ss-chatbot-history-item-info").addEventListener("click", () => {
                        loadConversationMessages(item.id);
                    });

                    // Click delete
                    itemDiv.querySelector(".ss-chatbot-history-delete-btn").addEventListener("click", (e) => {
                        e.stopPropagation();
                        if (confirm("Bạn có chắc chắn muốn xóa lịch sử cuộc trò chuyện này không?")) {
                            deleteConversation(item.id, itemDiv);
                        }
                    });

                    historyList.appendChild(itemDiv);
                });
            } else {
                historyList.innerHTML = `<div class="text-center py-4 text-danger small">Không thể tải lịch sử. Vui lòng đăng nhập lại.</div>`;
            }
        } catch (error) {
            console.error("Failed to load conversations:", error);
            historyList.innerHTML = `<div class="text-center py-4 text-danger small">Lỗi kết nối mạng.</div>`;
        }
    }

    // Load messages from a selected conversation (logged in user only)
    async function loadConversationMessages(id) {
        messagesContainer.innerHTML = `<div class="text-center py-5 text-muted small"><div class="spinner-border spinner-border-sm text-secondary me-2"></div>Đang tải tin nhắn...</div>`;
        historyPanel.classList.remove("active");
        currentConversationId = id;
        sessionStorage.setItem("shoestore_active_conv_id", id);

        try {
            const response = await fetch(`/api/chatbot/conversations/${id}/messages`);
            if (response.ok) {
                const data = await response.json();
                messagesContainer.innerHTML = "";

                if (data.length === 0) {
                    appendMessage("assistant", "Cuộc hội thoại trống.");
                } else {
                    data.forEach(msg => {
                        appendMessage(msg.role, msg.content);
                    });
                }
            } else {
                if (response.status === 403 || response.status === 401) {
                    // Stored conversation ID belongs to another user or session expired
                    sessionStorage.removeItem("shoestore_active_conv_id");
                    currentConversationId = null;
                    initChat();
                    return;
                }
                messagesContainer.innerHTML = "";
                appendMessage("assistant", "Có lỗi xảy ra khi tải tin nhắn từ lịch sử. Bạn vui lòng tải lại trang.");
            }
        } catch (error) {
            console.error("Failed to load messages:", error);
            messagesContainer.innerHTML = "";
            appendMessage("assistant", "Lỗi kết nối khi tải lịch sử tin nhắn.");
        }
    }

    // Delete a conversation (logged in user only)
    async function deleteConversation(id, element) {
        try {
            const response = await fetch(`/api/chatbot/conversations/${id}`, {
                method: "DELETE"
            });
            if (response.ok) {
                element.remove();
                if (currentConversationId === id || sessionStorage.getItem("shoestore_active_conv_id") === id.toString()) {
                    currentConversationId = null;
                    sessionStorage.removeItem("shoestore_active_conv_id");
                    initChat();
                }
                // Check if only the new chat button is left in the list (since button is 1 child)
                if (historyList.children.length <= 1) {
                    const noHistoryDiv = document.createElement("div");
                    noHistoryDiv.className = "text-center py-4 text-muted small";
                    noHistoryDiv.textContent = "Chưa có lịch sử cuộc trò chuyện nào.";
                    historyList.appendChild(noHistoryDiv);
                }
            } else {
                alert("Xóa hội thoại thất bại, vui lòng thử lại.");
            }
        } catch (error) {
            console.error("Failed to delete conversation:", error);
            alert("Lỗi kết nối khi thực hiện xóa.");
        }
    }
});
