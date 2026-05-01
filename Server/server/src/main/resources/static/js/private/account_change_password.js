document.getElementById('change-password-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');

    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!isValidPassword(newPassword)) {
        showAlert(PASSWORD_REQUIREMENTS, 'danger');
        return;
    }

    if (newPassword !== confirmPassword) {
        showAlert('New passwords do not match.', 'danger');
        return;
    }

    btn.disabled = true;

    const payload = {
        currentPassword: document.getElementById('currentPassword').value,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    fetch('/api/account/change-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => {
        showAlert('Password updated successfully.', 'success');
        document.getElementById('change-password-form').reset();
    })
    .catch(err => {
        showAlert(err || 'Failed to update password.', 'danger');
    })
    .finally(() => { btn.disabled = false; });
});
