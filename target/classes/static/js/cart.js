<<<<<<< HEAD
const API_CART = '/carts';
const API_ORDER = '/orders';

// Utility: Format currency
const formatVND = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

// Toast Notification
const showToast = (message, type = 'success') => {
    const existing = document.getElementById('custom-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'custom-toast';
    toast.className = `position-fixed top-0 start-50 translate-middle-x mt-4 p-3 rounded-3 shadow text-white d-flex align-items-center`;
    toast.style.zIndex = '9999';
    toast.style.backgroundColor = type === 'success' ? '#198754' : '#dc3545';
    
    const icon = document.createElement('i');
    icon.className = type === 'success' ? 'bi bi-check-circle-fill fs-5 me-2' : 'bi bi-exclamation-triangle-fill fs-5 me-2';
    
    const text = document.createElement('span');
    text.className = 'fw-medium';
    text.innerText = message;

    toast.appendChild(icon);
    toast.appendChild(text);
    document.body.appendChild(toast);

    toast.style.opacity = '0';
    toast.style.transform = 'translate(-50%, -20px)';
    toast.style.transition = 'all 0.3s ease';
    
    setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translate(-50%, 0)';
    }, 10);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translate(-50%, -20px)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
};

// Load Cart
const loadCart = async () => {
    try {
        const response = await fetch(API_CART);
        const data = await response.json();

        if (data.success) {
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
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="align-middle text-center">
                <input class="form-check-input fs-5 mt-0 cart-item-checkbox" type="checkbox" value="${item.id}" checked onchange="recalcTotal()">
            </td>
            <td class="align-middle ps-2">
                <div class="d-flex align-items-center">
                    <div>
                        <h6 class="mb-0 fw-bold">${item.sku || ('Variant #' + item.variantId)}</h6>
                        <small class="text-muted">Variant ID: ${item.variantId}</small>
                    </div>
                </div>
            </td>
            <td class="align-middle text-center">${formatVND(item.unitPrice)}</td>
            <td class="align-middle">
                <div class="input-group input-group-sm mx-auto" style="width: 110px;">
                    <button class="btn btn-outline-secondary" type="button" onclick="updateQuantity(${item.id}, ${item.quantity - 1})"><i class="bi bi-dash fs-6"></i></button>
                    <input type="text" class="form-control text-center fw-bold" value="${item.quantity}" readonly>
                    <button class="btn btn-outline-secondary" type="button" onclick="updateQuantity(${item.id}, ${item.quantity + 1})"><i class="bi bi-plus fs-6"></i></button>
                </div>
            </td>
            <td class="align-middle fw-bold text-danger text-end pe-4">${formatVND(item.itemTotal)}</td>
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
        const data = await response.json();

        if (response.ok && data.success) {
            if (successMessage) {
                showToast(successMessage, 'success');
            }
            loadCart();
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
        shippingAddress: document.getElementById('shippingAddress').value,
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
=======
>>>>>>> origin/member2-product-catalog
