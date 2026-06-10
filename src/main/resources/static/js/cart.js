const API_CART = '/carts';
const API_ORDER = '/api/orders';

// Utility: Format currency
const formatVND = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

// Toast Notification is handled globally by shop_layout.html

// Load Cart
window.updateGlobalCartBadge = async () => {
    try {
        const response = await fetch(API_CART);
        const data = await response.json();
        if (data.success && data.data.items) {
            const badge = document.getElementById('cart-badge');
            if (badge) {
                const totalItems = data.data.items.reduce((sum, item) => sum + item.quantity, 0);
                badge.innerText = totalItems;
                if (totalItems > 0) {
                    badge.classList.remove('d-none');
                } else {
                    badge.classList.add('d-none');
                }
                badge.style.transform = 'translate(-50%, -50%) scale(1.3)';
                setTimeout(() => { badge.style.transform = 'translate(-50%, -50%) scale(1)'; }, 200);
            }
        }
    } catch (e) { console.error('Error updating cart badge', e); }
};

const loadCart = async () => {
    try {
        const response = await fetch(API_CART);
        const data = await response.json();

        if (data.success) {
            updateGlobalCartBadge();
            if (typeof renderCart === 'function') {
                renderCart(data.data);
            }
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('Error loading cart:', error);
    }
};

window.currentCart = null;

let renderCart = (cart) => {
    window.currentCart = cart;
    const tbody = document.getElementById('cart-items');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (!cart.items || cart.items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center py-4 text-muted">Giỏ hàng trống</td></tr>';
        document.getElementById('cart-subtotal').innerText = '0 đ';
        return;
    }

    cart.items.forEach(item => {
        const imgSrc = item.imageUrl || 'https://via.placeholder.com/70?text=No+Image';
        const productName = item.productName || item.sku;
        const variantInfo = (item.colorName && item.sizeName) ? `${item.colorName} / Size ${item.sizeName}` : `Mã: ${item.sku}`;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="align-middle text-center">
                <input class="form-check-input fs-5 mt-0 cart-item-checkbox" type="checkbox" value="${item.id}" checked onchange="recalcTotal()" style="cursor: pointer; accent-color: var(--color-accent); margin-left: 0 !important; float: none !important;">
            </td>
            <td class="align-middle ps-2">
                <div class="d-flex align-items-center">
                    <img src="${imgSrc}" class="rounded-4 border border-light me-3 shadow-sm bg-white" style="width: 70px; height: 70px; object-fit: contain; padding: 4px;">
                    <div>
                        <h6 class="mb-1 fw-bold text-dark" style="font-size: 1rem; letter-spacing: -0.2px;">${productName}</h6>
                        <small class="text-muted d-block" style="font-size: 0.82rem;"><i class="bi bi-tag me-1 text-warning"></i>${variantInfo}</small>
                    </div>
                </div>
            </td>
            <td class="align-middle text-center fw-semibold text-secondary" style="font-size: 0.95rem;">${formatVND(item.unitPrice)}</td>
            <td class="align-middle">
                <div class="d-flex align-items-center justify-content-center">
                    <div class="d-flex align-items-center bg-light rounded-pill border p-1" style="height: 38px;">
                        <button class="btn btn-link text-secondary p-0 px-2 border-0 shadow-none" type="button" onclick="updateQuantity(${item.id}, ${item.quantity - 1})" style="transition: transform 0.2s;">
                            <i class="bi bi-dash-lg" style="font-size: 0.8rem;"></i>
                        </button>
                        <input type="text" class="form-control text-center fw-bold bg-transparent border-0 p-0 shadow-none" value="${item.quantity}" readonly style="width: 32px; font-size: 0.95rem; pointer-events: none; height: 100%;">
                        <button class="btn btn-link text-secondary p-0 px-2 border-0 shadow-none" type="button" onclick="updateQuantity(${item.id}, ${item.quantity + 1})" style="transition: transform 0.2s;">
                            <i class="bi bi-plus-lg" style="font-size: 0.8rem;"></i>
                        </button>
                    </div>
                </div>
            </td>
            <td class="align-middle text-end pe-4">
                <div class="d-flex align-items-center justify-content-end gap-3">
                    <span class="fw-bold text-dark" style="font-size: 1.05rem; color: #0f172a !important;">${formatVND(item.itemTotal)}</span>
                    <button class="btn btn-link text-danger p-0 border-0 shadow-none btn-hover-lift" onclick="updateQuantity(${item.id}, 0)" title="Xoá khỏi giỏ" style="font-size: 1.15rem; line-height: 1;">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Mặc định chọn tất cả
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) selectAllCheckbox.checked = true;
    recalcTotal();
};

window.toggleSelectAll = (el) => {
    const checkboxes = document.querySelectorAll('.cart-item-checkbox');
    checkboxes.forEach(cb => cb.checked = el.checked);
    recalcTotal();
};

window.recalcTotal = () => {
    if (!window.currentCart || !window.currentCart.items) return;
    
    const checkboxes = document.querySelectorAll('.cart-item-checkbox');
    let newSubTotal = 0;
    let allChecked = true;
    let anyChecked = false;
    
    checkboxes.forEach(cb => {
        if (cb.checked) {
            anyChecked = true;
            const itemId = parseInt(cb.value);
            const item = window.currentCart.items.find(i => i.id === itemId);
            if (item) {
                newSubTotal += item.itemTotal;
            }
        } else {
            allChecked = false;
        }
    });
    
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = allChecked && anyChecked;
    }
    
    const subTotalEl = document.getElementById('cart-subtotal');
    if (subTotalEl) {
        subTotalEl.innerText = formatVND(newSubTotal);
    }
};

// Confirm Modal Custom
const showConfirmModal = (message, onConfirm) => {
    const existing = document.getElementById('custom-confirm');
    if (existing) existing.remove();

    const overlay = document.createElement('div');
    overlay.id = 'custom-confirm';
    overlay.className = 'position-fixed w-100 h-100 top-0 start-0 d-flex align-items-center justify-content-center';
    overlay.style.backgroundColor = 'rgba(0,0,0,0.5)';
    overlay.style.zIndex = '9999';
    overlay.style.opacity = '0';
    overlay.style.transition = 'opacity 0.2s ease';

    const box = document.createElement('div');
    box.className = 'bg-white rounded-4 shadow-lg p-4 text-center mx-3';
    box.style.maxWidth = '350px';
    box.style.transform = 'scale(0.9)';
    box.style.transition = 'transform 0.2s ease';

    const icon = document.createElement('i');
    icon.className = 'bi bi-exclamation-circle text-warning mb-3 d-block';
    icon.style.fontSize = '3.5rem';

    const text = document.createElement('h6');
    text.className = 'mb-4 fw-bold';
    text.innerText = message;

    const btnRow = document.createElement('div');
    btnRow.className = 'd-flex justify-content-center gap-2';

    const btnCancel = document.createElement('button');
    btnCancel.className = 'btn btn-light fw-medium px-4';
    btnCancel.innerText = 'Huỷ';
    btnCancel.onclick = () => {
        overlay.style.opacity = '0';
        box.style.transform = 'scale(0.9)';
        setTimeout(() => overlay.remove(), 200);
    };

    const btnOk = document.createElement('button');
    btnOk.className = 'btn btn-danger fw-medium px-4';
    btnOk.innerText = 'Xoá';
    btnOk.onclick = () => {
        overlay.style.opacity = '0';
        box.style.transform = 'scale(0.9)';
        setTimeout(() => overlay.remove(), 200);
        onConfirm();
    };

    btnRow.appendChild(btnCancel);
    btnRow.appendChild(btnOk);
    box.appendChild(icon);
    box.appendChild(text);
    box.appendChild(btnRow);
    overlay.appendChild(box);
    document.body.appendChild(overlay);

    setTimeout(() => {
        overlay.style.opacity = '1';
        box.style.transform = 'scale(1)';
    }, 10);
};

// Update Quantity
window.updateQuantity = async (cartItemId, newQty) => {
    if (newQty < 1) {
        showConfirmModal('Bạn có chắc chắn muốn xoá sản phẩm này khỏi giỏ hàng?', async () => {
            await removeCartItem(cartItemId);
        });
        return;
    }
    await doUpdateCartItem(cartItemId, newQty);
};

const doUpdateCartItem = async (cartItemId, quantity) => {
    try {
        const response = await fetch(`${API_CART}/items/${cartItemId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity })
        });
        const data = await response.json();
        if (response.ok && data.success) {
            loadCart();
        } else {
            showToast(data.message || 'Lỗi cập nhật', 'error');
            loadCart();
        }
    } catch (error) {
        showToast('Lỗi kết nối máy chủ', 'error');
    }
};

const removeCartItem = async (cartItemId) => {
    try {
        const response = await fetch(`${API_CART}/items/${cartItemId}`, {
            method: 'DELETE'
        });
        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Đã xoá sản phẩm khỏi giỏ hàng!', 'success');
            loadCart();
        } else {
            showToast(data.message || 'Lỗi xóa sản phẩm', 'error');
        }
    } catch (error) {
        showToast('Lỗi kết nối máy chủ', 'error');
    }
};

// Clear Entire Cart
window.clearCart = async () => {
    showConfirmModal('Bạn có chắc chắn muốn xoá toàn bộ sản phẩm trong giỏ hàng?', async () => {
        try {
            const response = await fetch(API_CART, {
                method: 'DELETE'
            });
            const data = await response.json();
            if (response.ok && data.success) {
                showToast('Đã làm trống giỏ hàng!', 'success');
                loadCart();
            } else {
                showToast(data.message || 'Lỗi làm trống giỏ hàng', 'error');
            }
        } catch (error) {
            showToast('Lỗi kết nối máy chủ', 'error');
        }
    });
};

// Add to Cart Simulation
const addToCart = async (variantId, quantity, successMessage = 'Đã thêm vào giỏ hàng!') => {
    const btn = document.getElementById('btn-add-cart');
    if (btn) btn.classList.add('btn-loading');

    try {
        const response = await fetch(`${API_CART}/items`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ variantId: parseInt(variantId), quantity: parseInt(quantity) })
        });

        if (response.status === 401 || response.status === 403) {
            const currentUrl = encodeURIComponent(window.location.pathname + window.location.search);
            showGlobalConfirm(null, 'Vui lòng đăng nhập để tiếp tục mua sắm!', `/login?redirect=${currentUrl}`, 'Đăng nhập', 'Để sau');
            return;
        }

        const data = await response.json();

        if (response.ok && data.success) {
            if (successMessage) {
                showToast(successMessage, 'success');
            }
            updateGlobalCartBadge();
            if (document.getElementById('cart-items')) loadCart();
        } else {
            if (successMessage || !data.success) {
                showToast(data.message || 'Lỗi dữ liệu (Ví dụ: Variant không tồn tại hoặc hết hàng)', 'error');
            } else {
                loadCart();
            }
        }
    } catch (error) {
        showToast('Lỗi kết nối máy chủ', 'error');
    } finally {
        if (btn) btn.classList.remove('btn-loading');
    }
};

// Checkout Simulation
window.doCheckout = async (event) => {
    event.preventDefault();
    
    const isCartPage = window.location.pathname.includes('/cart-ui');
    
    if (isCartPage) {
        // Lấy các mục được chọn trên trang giỏ hàng
        const checkboxes = document.querySelectorAll('.cart-item-checkbox:checked');
        const selectedCartItemIds = Array.from(checkboxes).map(cb => parseInt(cb.value));
        
        if (selectedCartItemIds.length === 0) {
            showToast('Vui lòng chọn ít nhất một sản phẩm để thanh toán!', 'error');
            return;
        }
        
        // Lưu tạm vào session để trang checkout.html (GET) lấy ra hiển thị tóm tắt
        sessionStorage.setItem('selectedCartItemIds', JSON.stringify(selectedCartItemIds));
        window.location.href = '/checkout-ui';
        return;
    }
    
    // Nếu ở trang Checkout, tiến hành gọi API gửi đơn hàng
    const btn = document.getElementById('btn-checkout');
    if (btn) btn.classList.add('btn-loading');

    const requestData = {
        paymentMethod: document.getElementById('paymentMethod').value,
        shippingFullName: document.getElementById('shippingFullName').value,
        shippingPhone: document.getElementById('shippingPhone').value,
        shippingAddressLine: document.getElementById('shippingAddress').value,
        note: document.getElementById('note').value,
        selectedCartItemIds: JSON.parse(sessionStorage.getItem('selectedCartItemIds') || '[]')
    };

    if (requestData.selectedCartItemIds.length === 0) {
        showToast('Không có sản phẩm nào để thanh toán!', 'error');
        if (btn) btn.classList.remove('btn-loading');
        return;
    }

    try {
        const response = await fetch(`${API_ORDER}/checkout`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });
        const data = await response.json();

        if (response.ok && data.success) {
            sessionStorage.setItem('lastOrderCode', data.data.orderCode);
            sessionStorage.setItem('lastOrderTotal', formatVND(data.data.totalAmount));
            
            if (requestData.paymentMethod === 'BANK_TRANSFER') {
                window.location.href = `/payment-ui/${data.data.orderCode}`;
            } else {
                window.location.href = '/success-ui';
            }
        } else {
            showToast(data.message || 'Lỗi đặt hàng', 'error');
        }
    } catch (error) {
        showToast('Lỗi kết nối máy chủ', 'error');
    } finally {
        if (btn) btn.classList.remove('btn-loading');
    }
};

document.addEventListener('DOMContentLoaded', () => {
    updateGlobalCartBadge();
    
    if (document.getElementById('cart-items')) {
        loadCart();
    }

    const addForm = document.getElementById('form-add-to-cart');
    if (addForm) {
        addForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const vId = document.getElementById('input-variant-id').value;
            const qty = document.getElementById('input-quantity').value;
            addToCart(vId, qty);
        });
    }

    const checkoutForm = document.getElementById('form-checkout');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', doCheckout);
    }
});
