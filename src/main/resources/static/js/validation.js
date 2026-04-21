document.addEventListener('DOMContentLoaded', () => {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    function validateField(input) {
        if (input.value.trim().length > 2) {
            input.classList.remove('is-invalid');
        } else {
            input.classList.add('is-invalid');
        }
    }

    // ユーザー名のバリデーション
    usernameInput.addEventListener('input', () => validateField(usernameInput));

    // パスワードのバリデーション
    passwordInput.addEventListener('input', () => validateField(passwordInput));
});