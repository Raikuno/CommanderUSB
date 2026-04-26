
const POLL_INTERVAL_MS = 1 * 60 * 1000;

function buildCard(machine) {
    const template = document.getElementById('machine-card-template');
    const card = template.content.cloneNode(true);
    card.querySelector('[data-field="name"]').textContent = machine.name;
    card.querySelector('[data-field="ip"]').textContent = machine.ip;
    return card;
}

function renderMachines(machines) {
    const container = document.getElementById('unrevised-machines-container');
    container.innerHTML = '';
    machines.forEach(machine => container.appendChild(buildCard(machine)));
    return machines;
}

function changeTitle(machines){
    if(machines.length > 0){
        const title = document.getElementById('machines_title');
        console.log(title);
        title.textContent = 'Some machines require a revision';
    }
    
}

function fetchUnrevisedMachines() {
fetch('/api/machine/getUnrevisedMachines')
        .then(res => {
            if (!res.ok) throw new Error(res.status);
            return res.json();
        })
        .then(renderMachines)
        .then(changeTitle)
        .catch(err => console.error('Error fetching unrevised machines:', err));
}

fetchUnrevisedMachines();
setInterval(fetchUnrevisedMachines, POLL_INTERVAL_MS);

