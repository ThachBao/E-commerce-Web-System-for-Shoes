const API_CART = '/carts';
const API_ORDER = '/orders';

// Utility: Format currency
const formatVND = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

// Toast Notification
const showToast = (message, type = 'success') => {
    alert(`${type.toUpperCase()}: ${message}`);
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

let renderCart = (cart) => {
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
            <td class="align-middle">
                <div class="d-flex align-items-center">
                    <div class="ms-3">
                        <h6 class="mb-0 fw-bold">${item.sku || ('Variant #' + item.variantId)}</h6>
                        <small class="text-muted">Variant ID: ${item.variantId}</small>
                    </div>
                </div>
            </td>
            <td class="align-middle">${formatVND(item.unitPrice)}</td>
            <td class="align-middle">
                <span class="badge bg-secondary px-3 py-2 fs-6 rounded-pill">${item.quantity}</span>
            </td>
            <td class="align-middle fw-bold text-danger">${formatVND(item.itemTotal)}</td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById('cart-subtotal').innerText = formatVND(cart.subTotal);
};

// Add to Cart Simulation
const addToCart = async (variantId, quantity) => {
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
            showToast('Đã thêm vào giỏ hàng!', 'success');
            loadCart();
        } else {
            showToast(data.message || 'Lỗi dữ liệu (Ví dụ: Variant không tồn tại hoặc hết hàng)', 'error');
        }
    } catch (error) {
        showToast('Lỗi kết nối máy chủ', 'error');
    } finally {
        if (btn) btn.classList.remove('btn-loading');
    }
};

// Checkout Simulation
const doCheckout = async (event) => {
    event.preventDefault();
    const btn = document.getElementById('btn-checkout');
    if (btn) btn.classList.add('btn-loading');

    const requestData = {
        paymentMethod: document.getElementById('paymentMethod').value,
        shippingFullName: document.getElementById('shippingFullName').value,
        shippingPhone: document.getElementById('shippingPhone').value,
        shippingAddress: document.getElementById('shippingAddress').value,
        note: document.getElementById('note').value
    };

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
            window.location.href = '/success-ui';
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
