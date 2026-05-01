const form = document.getElementById('createAdminForm');
const submitBtn = document.getElementById('submitBtn');
form.addEventListener('submit', async function(e){
    e.preventDefault();
    submitBtn.disabled = true;

    const formData = new FormData(form);
    const email = formData.get('email');
    const name = formData.get('name');
    const password = formData.get('password');
    const confirm = formData.get('confirmPassword');

    if(!isValidEmail(email)){
        document.getElementById('email').classList.add('is-invalid');
        alert(EMAIL_REQUIREMENTS);
        submitBtn.disabled = false;
        return;
    }

    if(name === ""){
        document.getElementById('name').classList.add('is-invalid');
        alert('Name is required');
        submitBtn.disabled = false;
        return;
    }

    if(!isValidPassword(password)){
        document.getElementById('password').classList.add('is-invalid');
        alert(PASSWORD_REQUIREMENTS);
        submitBtn.disabled = false;
        return;
    }

    if(password !== confirm){
        alert('Make sure that both passwords are the same');
        submitBtn.disabled = false;
        return;
    }

    const payload = {};
    for(const [key, value] of formData.entries()){
        if(key === 'confirmPassword') continue;
        payload[key] = value;
    }

    const csrfToken = document.getElementById('_csrf') ? document.getElementById('_csrf').value : null;

    try{
            const res = await fetch(form.getAttribute('action') || '/api/auth/firstuser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(payload)
        });

        if(!res.ok){
            const text = await res.text();
            alert('Error: ' + (text || res.statusText));
            submitBtn.disabled = false;
            return;
        }

        window.location.href = '/session/login';
    }catch(err){
        console.error(err);
        alert('An error has occurred');
        submitBtn.disabled = false;
    }
});