const LOG_TYPES = {
    1001: { label: 'Information',             cssClass: 'log-info'     },
    1002: { label: 'Configuration Modified',  cssClass: 'log-danger'   },
    1003: { label: 'Registry Modified',       cssClass: 'log-danger'   },
    1004: { label: 'Incoherent State',        cssClass: 'log-warning'  },
    1005: { label: 'Memory Connection Issue', cssClass: 'log-warning'  },
    1006: { label: 'Connection Issue',        cssClass: 'log-warning'  },
    1007: { label: 'Critical Error',          cssClass: 'log-critical' }
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

function formatFrecuency(ms) {
    if (ms == null) return '—';
    return `${ms} ms`;
}

function dateToMs(date) {
    if (!date) return 0;
    if (Array.isArray(date)) {
        const [y, mo, d, h, m, s] = date;
        return new Date(y, (mo || 1) - 1, d || 1, h || 0, m || 0, s || 0).getTime();
    }
    return new Date(date).getTime();
}

function applyIconColor(machine) {
    const box = document.getElementById('machine-icon-box');
    box.classList.remove('log-info','log-danger','log-warning','log-critical','machine-disabled');
    if (machine.disable) { box.classList.add('machine-disabled'); return; }
    const code = machine.topUnrevisedLogCode;
    if (code != null && LOG_TYPES[code]) {
        box.classList.add(LOG_TYPES[code].cssClass);
    }
}

let currentMachine = null;

function renderMachine(machine) {
    currentMachine = machine;
    document.getElementById('machine-name').textContent = machine.name || '—';
    document.getElementById('machine-ip').textContent   = machine.ip   || '—';
    document.getElementById('info-log-frecuency').textContent = formatFrecuency(machine.logFrecuency);
    document.getElementById('info-reg-dt').textContent        = formatDate(machine.registeredDate);
    document.getElementById('info-description').textContent   = machine.description || '—';

    document.getElementById('cfg-log-frec').value   = machine.logFrecuency ?? '';
    document.getElementById('cfg-enable-usb').checked = false;
    document.getElementById('cfg-enable-for').value = '';

    const toggleBtn = document.getElementById('toggle-disable-btn');
    if (machine.disable) {
        toggleBtn.textContent = 'Enable';
        toggleBtn.classList.remove('btn-warning');
        toggleBtn.classList.add('btn-success');
    } else {
        toggleBtn.textContent = 'Disable';
        toggleBtn.classList.remove('btn-success');
        toggleBtn.classList.add('btn-warning');
    }

    applyIconColor(machine);
}

function renderLogs(logs) {
    const tbody = document.getElementById('logs-tbody');
    if (!logs.length) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No logs found.</td></tr>';
        return;
    }
    logs = logs.slice().sort((a, b) => dateToMs(b.creationDate) - dateToMs(a.creationDate));
    tbody.innerHTML = logs.map(log => {
        const type = LOG_TYPES[log.logCode] || { label: `Code ${log.logCode}`, cssClass: '' };
        const isInfo = log.logCode === 1001;
        const rowClass = (log.needsRevission || isInfo) ? type.cssClass : 'log-revised';
        return `
        <tr class="${rowClass}">
            <td>${escapeHtml(log.machine?.name)}</td>
            <td>${formatDate(log.creationDate)}</td>
            <td>${escapeHtml(type.label)}</td>
            <td>
                <a href="/machines/${window.MACHINE_ID}/logs/${log.id}" class="btn btn-sm btn-outline-primary">Details</a>
            </td>
        </tr>`;
    }).join('');
}

function renderErrorLogs(errorLogs) {
    const tbody = document.getElementById('error-logs-tbody');
    if (!errorLogs.length) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">No error logs found.</td></tr>';
        return;
    }
    tbody.innerHTML = errorLogs.map(e => `
        <tr>
            <td>${formatDate(e.recievedDate)}</td>
            <td>${formatDate(e.creationDate)}</td>
            <td>${escapeHtml(e.message)}</td>
        </tr>`).join('');
}

function loadMachine() {
    fetch(`/api/machine/${window.MACHINE_ID}`)
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderMachine)
        .catch(() => showAlert('Could not load machine data.', 'danger'));
}

function loadLogs() {
    fetch(`/api/logs/by-machine/${window.MACHINE_ID}`)
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderLogs)
        .catch(() => {
            document.getElementById('logs-tbody').innerHTML =
                '<tr><td colspan="4" class="text-center text-danger py-3">Failed to load logs.</td></tr>';
        });
}

function loadErrorLogs() {
    fetch(`/api/machine/${window.MACHINE_ID}/error-logs`)
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderErrorLogs)
        .catch(() => {
            document.getElementById('error-logs-tbody').innerHTML =
                '<tr><td colspan="3" class="text-center text-danger py-3">Failed to load error logs.</td></tr>';
        });
}

document.getElementById('config-form').addEventListener('submit', function (e) {
    e.preventDefault();
    const body = {
        logFrecuency: document.getElementById('cfg-log-frec').value,
        enableUsb:    document.getElementById('cfg-enable-usb').checked,
        enableFor:    document.getElementById('cfg-enable-for').value
    };
    fetch(`/api/machine/${window.MACHINE_ID}/change-machine-conf`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
    .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
    .then(() => showAlert('Configuration was sent.', 'success'))
    .catch(err => showAlert(err || 'Failed to send configuration.', 'danger'));
});

function patchMachine(updates) {
    return fetch(`/api/machine/${window.MACHINE_ID}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updates)
    }).then(res => res.ok ? res.json() : res.text().then(t => Promise.reject(t)));
}

function setNameEditMode(on) {
    document.getElementById('machine-name-view').classList.toggle('d-none', on);
    document.getElementById('machine-name-edit').classList.toggle('d-none', !on);
    if (on) {
        const input = document.getElementById('edit-name-input');
        input.value = currentMachine ? (currentMachine.name || '') : '';
        input.focus();
        input.select();
    }
}

function setDescriptionEditMode(on) {
    document.getElementById('machine-description-view').classList.toggle('d-none', on);
    document.getElementById('machine-description-edit').classList.toggle('d-none', !on);
    if (on) {
        const input = document.getElementById('edit-description-input');
        input.value = currentMachine ? (currentMachine.description || '') : '';
        input.focus();
    }
}

document.getElementById('edit-name-btn').addEventListener('click', () => setNameEditMode(true));
document.getElementById('cancel-name-btn').addEventListener('click', () => setNameEditMode(false));
document.getElementById('save-name-btn').addEventListener('click', () => {
    const name = document.getElementById('edit-name-input').value.trim();
    if (!name) { showAlert('Name cannot be blank.', 'warning'); return; }
    patchMachine({ name })
        .then(updated => { renderMachine(updated); setNameEditMode(false); })
        .catch(err => showAlert(err || 'Failed to update name.', 'danger'));
});

document.getElementById('edit-description-btn').addEventListener('click', () => setDescriptionEditMode(true));
document.getElementById('cancel-description-btn').addEventListener('click', () => setDescriptionEditMode(false));
document.getElementById('save-description-btn').addEventListener('click', () => {
    const description = document.getElementById('edit-description-input').value;
    patchMachine({ description })
        .then(updated => { renderMachine(updated); setDescriptionEditMode(false); })
        .catch(err => showAlert(err || 'Failed to update description.', 'danger'));
});

document.getElementById('toggle-disable-btn').addEventListener('click', function () {
    if (!currentMachine) return;
    const btn = this;
    btn.disabled = true;
    const newDisable = !currentMachine.disable;
    patchMachine({ disable: newDisable })
        .then(updated => {
            renderMachine(updated);
            showAlert(newDisable ? 'Machine disabled.' : 'Machine enabled.', 'success');
        })
        .catch(err => showAlert(err || 'Failed to toggle machine state.', 'danger'))
        .finally(() => { btn.disabled = false; });
});

loadMachine();
loadLogs();
loadErrorLogs();
