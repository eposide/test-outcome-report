document.addEventListener('DOMContentLoaded', function () {

    console.log('User menu script loaded');
    const userMenuButton = document.getElementById('userMenuButton');
    const userMenuDropdown = document.getElementById('userMenuDropdown');

    if (!userMenuButton || !userMenuDropdown) {
        return;
    }

    userMenuButton.addEventListener('click', function (event) {
        event.stopPropagation();

        const isOpen = userMenuDropdown.classList.toggle('show');

        userMenuButton.setAttribute('aria-expanded', isOpen);
    });

    document.addEventListener('click', function (event) {

        if (!event.target.closest('#userMenu')) {
            userMenuDropdown.classList.remove('show');
            userMenuButton.setAttribute('aria-expanded', 'false');
        }

    });
});