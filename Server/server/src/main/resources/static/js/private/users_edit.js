function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function loadRoles(currentRoleId) {
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
                if (currentRoleId && r.id === currentRoleId) opt.selected = true;
                select.appendChild(opt);
            });
        })
        .catch(() => showAlert('Could not load roles.', 'warning'));
}

function loadUser() {
    fetch(`/api/users/${window.USER_ID}`)
        .then(res => {
            if (!res.ok) throw new Error(res.status);
            return res.json();
        })
        .then(user => {
            document.getElementById('name').value = user.name;
            document.getElementById('email').value = user.email;
            loadRoles(user.role ? user.role.id : null);
        })
        .catch(() => showAlert('Could not load user data.', 'danger'));
}

document.getElementById('edit-user-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const btn = document.getElementById('submit-btn');
    btn.disabled = true;

    const payload = {
        name: document.getElementById('name').value.trim(),
        email: document.getElementById('email').value.trim(),
        roleId: document.getElementById('role').value || null
    };

    fetch(`/api/users/${window.USER_ID}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => { window.location.href = '/users'; })
    .catch(err => {
        showAlert(err || 'Failed to update user.', 'danger');
        btn.disabled = false;
    });
});

loadUser();
