function loadRoles() {
    fetch('/api/users/roles')
        .then(res => {
            if (!res.ok) throw new Error(res.status);
            return res.json();
        })
        .then(roles => {
            const select = document.getElementById('role');
            roles.forEach(r => {
                const opt = document.createElement('option');
                opt.value = r.id;
                opt.textContent = r.name;
                select.appendChild(opt);
            });
        })
        .catch(() => showAlert('Could not load roles.', 'warning'));
}

document.getElementById('new-user-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');

    const payload = {
        name: document.getElementById('name').value.trim(),
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value,
        roleId: document.getElementById('role').value || null
    };

    if (!isValidEmail(payload.email)) {
        showAlert(EMAIL_REQUIREMENTS, 'danger');
        return;
    }

    if (!isValidPassword(payload.password)) {
        showAlert(PASSWORD_REQUIREMENTS, 'danger');
        return;
    }

    btn.disabled = true;

    fetch('/api/users/createUser', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => { alert('User created successfully'); window.location.href = '/users'; })
    .catch(err => {
        showAlert(err || 'Failed to create user.', 'danger');
        btn.disabled = false;
    });
});

loadRoles();
