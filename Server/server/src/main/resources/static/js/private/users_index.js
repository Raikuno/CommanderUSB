function renderUsers(users) {
    const tbody = document.getElementById('users-tbody');
    if (!users.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No users found</td></tr>';
        return;
    }
    tbody.innerHTML = users.map(u => `
        <tr>
            <td>${escapeHtml(u.name)}</td>
            <td>${escapeHtml(u.email)}</td>
            <td>${u.role ? escapeHtml(u.role.name) : '<span class="text-muted">—</span>'}</td>
            <td>
                <a href="/users/${u.id}/edit" class="btn btn-sm btn-outline-primary">Edit</a>
            </td>
            <td>
                <span class="badge ${u.disable ? 'bg-danger' : 'bg-success'}">
                    ${u.disable ? 'Disabled' : 'Active'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm ${u.disable ? 'btn-success' : 'btn-warning'}"
                        onclick="toggleDisable('${u.id}', this)">
                    ${u.disable ? 'Enable' : 'Disable'}
                </button>
            </td>
        </tr>
    `).join('');
}

function toggleDisable(id, btn) {
    btn.disabled = true;
    fetch(`/api/users/users/${id}/disable`, { method: 'PATCH' })
        .then(res => {
            if (!res.ok) throw new Error(res.status);
            return res.json();
        })
        .then(() => loadUsers())
        .catch(() => {
            showAlert('Failed to update user status.', 'danger');
            btn.disabled = false;
        });
}

function loadUsers() {
    fetch('/api/users')
        .then(res => {
            if (!res.ok) throw new Error(res.status);
            return res.json();
        })
        .then(renderUsers)
        .catch(() => showAlert('Failed to load users.', 'danger'));
}

loadUsers();
