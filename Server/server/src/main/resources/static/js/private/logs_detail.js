const LOG_TYPES = {
    1001: { label: 'Information',             cssClass: 'log-info',     description: 'Information log. No weird activity found' },
    1002: { label: 'Configuration Modified',  cssClass: 'log-danger',   description: 'Someone tried to change the configuration of the application saved on the registry' },
    1003: { label: 'Registry Modified',       cssClass: 'log-danger',   description: 'Someone tried to modify the values related to usb drive mounting in the registry' },
    1004: { label: 'Incoherent State',        cssClass: 'log-warning',  description: 'An incoherence was found between the expected values and the ones found in the registry' },
    1005: { label: 'Memory Connection Issue', cssClass: 'log-warning',  description: 'An usb drive seems to have been connected to the machine' },
    1006: { label: 'Connection Issue',        cssClass: 'log-warning',  description: 'A log couldnt be sent because of a connection issue' },
    1007: { label: 'Application Error',       cssClass: 'log-critical', description: 'An error occurred in the application' }
};

function usbValueMessage(val) {
    if (val === 3) return `${val} — this means the machine would allow a USB to be mounted`;
    if (val === 4) return `${val} — this means the machine would not be able to mount USB drives`;
    return `${val}`;
}

function usbAllowedMessage(allowed) {
    return allowed
        ? 'USB were allowed to be mounted on the machine'
        : 'USB were not allowed to be mounted on the machine';
}

function renderLog(log) {
    const type = LOG_TYPES[log.logCode] || { label: `Code ${log.logCode}`, cssClass: '', description: '[unknown]' };

    document.getElementById('machine-name').textContent = log.machine?.name ?? '—';
    document.getElementById('machine-ip').textContent   = log.machine?.ip   ?? '—';

    const container = document.getElementById('log-container');
    const isInfo = log.logCode === 1001;
    const colorClass = (log.needsRevission || isInfo) ? type.cssClass : 'log-revised';
    container.className = `log-detail-container mb-4 ${colorClass}`;

    const rows = [
        ['Code',          `Code ${log.logCode}, ${escapeHtml(type.description)}`],
        ['USB Value',     escapeHtml(usbValueMessage(log.usbValue))],
        ['USB Allowed',   escapeHtml(usbAllowedMessage(log.usbAllowed))],
        ['Creation Date', escapeHtml(formatDate(log.creationDate))],
        ['Received Date', escapeHtml(formatDate(log.recievedDate))]
    ];

    document.getElementById('log-detail-tbody').innerHTML = rows
        .map(([label, value]) => `
            <tr>
                <td><strong>${label}</strong></td>
                <td>${value}</td>
            </tr>`)
        .join('');

    if(document.getElementById('revise-btn') !== null){
        document.getElementById('revise-btn').disabled = !log.needsRevission;
    }
}

function loadLog() {
    fetch(`/api/logs/${window.LOG_ID}`)
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(renderLog)
        .catch(() => showAlert('Could not load log data.', 'danger'));
}

if(document.getElementById('revise-btn') !== null){
    document.getElementById('revise-btn').addEventListener('click', function () {
        new bootstrap.Modal(document.getElementById('reviseModal')).show();
    });
}

if(document.getElementById('confirm-revise-btn') !== null){
    document.getElementById('confirm-revise-btn').addEventListener('click', function () {
        const btn = this;
        btn.disabled = true;

        fetch(`/api/logs/${window.LOG_ID}/revise`, { method: 'PATCH' })
            .then(res => res.ok ? res : res.text().then(t => Promise.reject(t)))
            .then(() => {
                bootstrap.Modal.getInstance(document.getElementById('reviseModal')).hide();
                window.location.href = window.BACK_URL || '/logs';
            })
            .catch(err => {
                bootstrap.Modal.getInstance(document.getElementById('reviseModal')).hide();
                showAlert(err || 'Failed to mark log as revised.', 'danger');
                btn.disabled = false;
            });
    });
}


loadLog();
