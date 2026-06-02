/* ==========================================================================
   SHOE STORE GLOBAL PREMIUM DYNAMIC EFFECTS (JS)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
    
    // ----------------------------------------------------------------------
    // 1. Dynamic Glassmorphic Navbar on Scroll
    // ----------------------------------------------------------------------
    const navbar = document.querySelector('.navbar');
    
    const handleNavbarScroll = () => {
        if (!navbar) return;
        if (window.scrollY > 30) {
            navbar.classList.add('navbar-scrolled');
        } else {
            navbar.classList.remove('navbar-scrolled');
        }
    };

    // Run on init and attach scroll listener
    handleNavbarScroll();
    window.addEventListener('scroll', handleNavbarScroll);

    // ----------------------------------------------------------------------
    // 2. High-Performance IntersectionObserver for Scroll Reveals
    // ----------------------------------------------------------------------
    const revealElements = document.querySelectorAll('.reveal');
    
    if ('IntersectionObserver' in window && revealElements.length > 0) {
        const revealOptions = {
            threshold: 0.08,
            rootMargin: '0px 0px -40px 0px' // Trigger slightly before coming into view
        };

        const revealObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('active');
                    // Stop observing once revealed to optimize performance
                    observer.unobserve(entry.target);
                }
            });
        }, revealOptions);

        revealElements.forEach(element => {
            // Check if element is already in viewport initially
            const rect = element.getBoundingClientRect();
            if (rect.top < window.innerHeight && rect.bottom > 0) {
                element.classList.add('active');
            } else {
                revealObserver.observe(element);
            }
        });
    } else {
        // Fallback for older browsers
        revealElements.forEach(element => element.classList.add('active'));
    }

    // ----------------------------------------------------------------------
    // 3. Smooth Image Switcher for Detail Page Gallery
    // ----------------------------------------------------------------------
    const mainImage = document.getElementById('mainImage');
    const galleryImages = document.querySelectorAll('.gallery-img');

    if (mainImage && galleryImages.length > 0) {
        galleryImages.forEach(img => {
            // Remove the default inline click if we want to handle it dynamically
            img.removeAttribute('onclick'); 
            
            img.addEventListener('click', function(e) {
                e.preventDefault();
                const newSrc = this.src;
                
                // Toggle active state for thumbnails
                galleryImages.forEach(thumb => thumb.classList.remove('active'));
                this.classList.add('active');
                
                // Fade out main image
                mainImage.classList.add('image-fading');
                
                setTimeout(() => {
                    mainImage.src = newSrc;
                    // Wait for image loading before fading back in
                    mainImage.onload = () => {
                        mainImage.classList.remove('image-fading');
                    };
                }, 150); // Match CSS fade speed
            });
        });
        
        // Add active class to first thumbnail initially
        if (galleryImages[0]) galleryImages[0].classList.add('active');
    }

    // ----------------------------------------------------------------------
    // 4. Global Interactivity - Button Click Ripple & Hover Lifts
    // ----------------------------------------------------------------------
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(btn => {
        // If it's a primary/dark/warning action, give it a hover-lift class if it doesn't have it
        if (btn.classList.contains('btn-warning') || btn.classList.contains('btn-dark') || btn.classList.contains('btn-primary')) {
            btn.classList.add('btn-hover-lift');
        }
    });

    // ----------------------------------------------------------------------
    // 5. Dynamic Sidebar Active Links Highlighter (Admin Panel & Shop Links)
    // ----------------------------------------------------------------------
    const currentPath = window.location.pathname;
    
    // Highlight shop navbar active state dynamically
    const shopNavLinks = document.querySelectorAll('.navbar-nav .nav-link');
    shopNavLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (href !== '/' && currentPath.startsWith(href))) {
            link.classList.add('active');
        }
    });

    // Highlight admin sidebar menu active state dynamically
    const adminSidebarLinks = document.querySelectorAll('.sidebar .nav-link, .sidebar a');
    adminSidebarLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && href !== '#' && (href === currentPath || currentPath.startsWith(href))) {
            link.classList.add('active');
            
            // Auto-expand parent submenu if nested
            const parentCollapse = link.closest('.collapse');
            if (parentCollapse) {
                parentCollapse.classList.add('show');
                const submenuTrigger = document.querySelector(`[href="#${parentCollapse.id}"]`);
                if (submenuTrigger) {
                    submenuTrigger.setAttribute('aria-expanded', 'true');
                    submenuTrigger.classList.add('active');
                }
            }
        }
    });
});

// --------------------------------------------------------------------------
// 5. Cart Badge Dynamic Bouncing Event
// --------------------------------------------------------------------------
window.triggerCartBadgeBounce = () => {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    
    // Add animation class
    badge.classList.add('cart-badge-bounce');
    
    // Remove class after animation finishes to allow re-triggering
    setTimeout(() => {
        badge.classList.remove('cart-badge-bounce');
    }, 550); // Matches the 0.5s bounce animation
};

// Hook into existing cart badge updates to automatically animate it
(function() {
    const originalUpdateBadge = window.updateGlobalCartBadge;
    if (typeof originalUpdateBadge === 'function') {
        window.updateGlobalCartBadge = async function() {
            const badge = document.getElementById('cart-badge');
            const oldVal = badge ? badge.innerText : '0';
            
            await originalUpdateBadge.apply(this, arguments);
            
            const newVal = badge ? badge.innerText : '0';
            // Only bounce if the cart items count actually changed
            if (oldVal !== newVal) {
                window.triggerCartBadgeBounce();
            }
        };
    }
})();
