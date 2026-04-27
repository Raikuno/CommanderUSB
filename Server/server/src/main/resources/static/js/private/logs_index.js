const LOG_TYPES = {
    1001: { label: 'Information',             cssClass: 'log-info'     },
    1002: { label: 'Configuration Modified',  cssClass: 'log-danger'   },
    1003: { label: 'Registry Modified',       cssClass: 'log-danger'   },
    1004: { label: 'Incoherent State',        cssClass: 'log-warning'  },
    1005: { label: 'Memory Connection Issue', cssClass: 'log-warning'  },
    1006: { label: 'Connection Issue',        cssClass: 'log-warning'  },
    1007: { label: 'Application Error',       cssClass: 'log-critical' }
};

function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function formatDate(date) {
    if (!date) return '—';
    if (Array.isArray(date)) {
        const [y, mo, d, h, m, s] = date;
        return `${y}-${String(mo).padStart(2,'0')}-${String(d).padStart(2,'0')} `
             + `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s||0).padStart(2,'0')}`;
    }
    return new Date(date).toLocaleString();
}

function renderLogs(logs) {
    const tbody = document.getElementById('logs-tbody');
    if (!logs.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-3">No unrevised logs found.</td></tr>';
        return;
    }
    tbody.innerHTML = logs.map(log => {
        const type = LOG_TYPES[log.logCode] || { label: `Code ${log.logCode}`, cssClass: '' };
        return `
        <tr class="${type.cssClass}">
            <td>${escapeHtml(log.machine?.name)}</td>
            <td>${formatDate(log.creationDate)}</td>
            <td>${escapeHtml(type.label)}</td>
            <td>
                <a href="/machines/${escapeHtml(log.machine?.id)}"
                   class="btn btn-sm btn-outline-secondary">Machine</a>
            </td>
            <td>
                <a href="/logs/${log.id}" class="btn btn-sm btn-outline-primary">Details</a>
            </td>
            <td>
                <input type="checkbox" class="log-checkbox" value="${log.id}">
            </td>
        </tr>`;
    }).join('');

    document.querySelectorAll('.log-checkbox').forEach(cb =>
        cb.addEventListener('change', updateSelectAll)
    );
}

function updateSelectAll() {
    const all  = document.querySelectorAll('.log-checkbox');
    const checked = document.querySelectorAll('.log-checkbox:checked');
    document.getElementById('select-all').indeterminate = checked.length > 0 && checked.length < all.length;
    document.getElementById('select-all').checked = all.length > 0 && checked.length === all.length;
}

function loadLogs() {
    fetch('/api/logs/unrevised')
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderLogs)
        .catch(() => showAlert('Failed to load logs.', 'danger'));
}

document.getElementById('select-all').addEventListener('change', function () {
    document.querySelectorAll('.log-checkbox').forEach(cb => cb.checked = this.checked);
});

document.getElementById('mark-revised-btn').addEventListener('click', function () {
    const selected = [...document.querySelectorAll('.log-checkbox:checked')];
    if (selected.length === 0) {
        showAlert('Select at least one log to mark as revised.', 'warning');
        return;
    }
    document.getElementById('revise-count').textContent = selected.length;
    new bootstrap.Modal(document.getElementById('reviseModal')).show();
});

document.getElementById('confirm-revise-btn').addEventListener('click', function () {
    const btn = this;
    btn.disabled = true;
    const ids = [...document.querySelectorAll('.log-checkbox:checked')].map(cb => parseInt(cb.value));

    fetch('/api/logs/revise-bulk', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(ids)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => {
        bootstrap.Modal.getInstance(document.getElementById('reviseModal')).hide();
        loadLogs();
    })
    .catch(err => {
        bootstrap.Modal.getInstance(document.getElementById('reviseModal')).hide();
        showAlert(err || 'Failed to mark logs as revised.', 'danger');
    })
    .finally(() => { btn.disabled = false; });
});

loadLogs();
