const LOG_TYPES = {
    1001: { label: 'Information',             cssClass: 'log-info'     },
    1002: { label: 'Configuration Modified',  cssClass: 'log-danger'   },
    1003: { label: 'Registry Modified',       cssClass: 'log-danger'   },
    1004: { label: 'Incoherent State',        cssClass: 'log-warning'  },
    1005: { label: 'Memory Connection Issue', cssClass: 'log-warning'  },
    1006: { label: 'Connection Issue',        cssClass: 'log-warning'  },
    1007: { label: 'Application Error',       cssClass: 'log-critical' }
};

const canRevise = document.getElementById('mark-revised-btn') !== null;

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
            <td>${canRevise ? `<input type="checkbox" class="log-checkbox" value="${log.id}">` : ''}</td>
        </tr>`;
    }).join('');

    if (canRevise) {
        document.querySelectorAll('.log-checkbox').forEach(cb =>
            cb.addEventListener('change', updateSelectAll)
        );
    }
}

function updateSelectAll() {
    const all = document.querySelectorAll('.log-checkbox');
    const checked = document.querySelectorAll('.log-checkbox:checked');
    const selectAll = document.getElementById('select-all');
    selectAll.indeterminate = checked.length > 0 && checked.length < all.length;
    selectAll.checked = all.length > 0 && checked.length === all.length;
}

function loadLogs() {
    fetch('/api/logs/unrevised')
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderLogs)
        .catch(() => showAlert('Failed to load logs.', 'danger'));
}

const selectAll = document.getElementById('select-all');
if (selectAll) {
    selectAll.addEventListener('change', function () {
        document.querySelectorAll('.log-checkbox').forEach(cb => cb.checked = this.checked);
    });
}

const markRevisedBtn = document.getElementById('mark-revised-btn');
if (markRevisedBtn) {
    markRevisedBtn.addEventListener('click', function () {
        const selected = [...document.querySelectorAll('.log-checkbox:checked')];
        if (selected.length === 0) {
            showAlert('Select at least one log to mark as revised.', 'warning');
            return;
        }
        document.getElementById('revise-count').textContent = selected.length;
        new bootstrap.Modal(document.getElementById('reviseModal')).show();
    });
}

const confirmReviseBtn = document.getElementById('confirm-revise-btn');
if (confirmReviseBtn) {
    confirmReviseBtn.addEventListener('click', function () {
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
}

loadLogs();
