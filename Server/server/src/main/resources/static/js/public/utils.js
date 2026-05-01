const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

const PASSWORD_REQUIREMENTS =
    'Password must be at least 8 characters and include an uppercase letter, a lowercase letter and a digit';
const EMAIL_REQUIREMENTS = 'Email format is not valid';

function isValidEmail(value) {
    return typeof value === 'string' && EMAIL_REGEX.test(value);
}

function isValidPassword(value) {
    return typeof value === 'string' && PASSWORD_REGEX.test(value);
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function showAlert(msg, type) {
    document.getElementById('alert-container').innerHTML =
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${escapeHtml(msg)}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
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

function getCheckedPermissions() {
    return Array.from(
        document.querySelectorAll('#permissions-container input[type=checkbox]:checked')
    ).map(cb => cb.value);
}
