const LOG_TYPES = {
    1001: { label: 'Information',             cssClass: 'log-info'     },
    1002: { label: 'Configuration Modified',  cssClass: 'log-danger'   },
    1003: { label: 'Registry Modified',       cssClass: 'log-danger'   },
    1004: { label: 'Incoherent State',        cssClass: 'log-warning'  },
    1005: { label: 'Memory Connection Issue', cssClass: 'log-warning'  },
    1006: { label: 'Connection Issue',        cssClass: 'log-warning'  },
    1007: { label: 'Critical Error',          cssClass: 'log-critical' }
};

let allMachines = [];

function colorClassFor(machine) {
    if (machine.disable) return 'machine-disabled';
    const code = machine.topUnrevisedLogCode;
    if (code == null) return '';
    return (LOG_TYPES[code] && LOG_TYPES[code].cssClass) || '';
}

function applyFilters() {
    const nameQuery = document.getElementById('filter-name').value.trim().toLowerCase();
    const ipQuery   = document.getElementById('filter-ip').value.trim().toLowerCase();
    const onlyConnected = document.getElementById('filter-connected').checked;
    const showDisabled  = document.getElementById('filter-show-disabled').checked;
    const onlyNeedsRev  = document.getElementById('filter-needs-revision').checked;

    return allMachines.filter(m => {
        if (!showDisabled && m.disable) return false;
        if (onlyConnected && !m.connected) return false;
        if (onlyNeedsRev && m.topUnrevisedLogCode == null) return false;
        if (nameQuery && !(m.name || '').toLowerCase().includes(nameQuery)) return false;
        if (ipQuery && !(m.ip || '').toLowerCase().includes(ipQuery)) return false;
        return true;
    });
}

function renderGrid() {
    const grid = document.getElementById('machines-grid');
    const empty = document.getElementById('machines-empty');
    const machines = applyFilters();

    grid.innerHTML = '';
    if (machines.length === 0) {
        const msg = document.createElement('div');
        msg.id = 'machines-empty';
        msg.className = 'col-12 text-center text-muted py-3';
        msg.textContent = 'No machines match the current filters.';
        grid.appendChild(msg);
        return;
    }

    const tpl = document.getElementById('machine-card-template');
    machines.forEach(m => {
        const node = tpl.content.firstElementChild.cloneNode(true);
        const link = node.querySelector('.machine-card-link');
        const card = node.querySelector('.machine-card');
        const icon = node.querySelector('.machine-card-icon');
        const nameEl = node.querySelector('.machine-card-name');
        const ipEl = node.querySelector('.machine-card-ip');

        link.setAttribute('href', `/machines/${m.id}`);
        nameEl.textContent = m.name || '';
        ipEl.textContent = m.ip || '';

        const colorClass = colorClassFor(m);
        if (colorClass) card.classList.add(colorClass);
        if (m.disable) icon.classList.add('machine-disabled');

        if (m.connected) {
            const badge = document.createElement('span');
            badge.className = 'badge bg-success machine-card-status';
            badge.textContent = 'Connected';
            card.appendChild(badge);
        }

        grid.appendChild(node);
    });
}

function loadMachines() {
    fetch('/api/machine')
        .then(res => { if (!res.ok) throw new Error(res.status); return res.json(); })
        .then(data => { allMachines = data; renderGrid(); })
        .catch(() => showAlert('Failed to load machines.', 'danger'));
}

['filter-name', 'filter-ip'].forEach(id => {
    document.getElementById(id).addEventListener('input', renderGrid);
});
['filter-connected', 'filter-show-disabled', 'filter-needs-revision'].forEach(id => {
    document.getElementById(id).addEventListener('change', renderGrid);
});

document.getElementById('filter-form').addEventListener('submit', e => e.preventDefault());

loadMachines();
