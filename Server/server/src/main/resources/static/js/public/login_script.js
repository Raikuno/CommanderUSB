const form = document.getElementById('loginForm');
const submitBtn = document.getElementById('submitBtn');
form.addEventListener('submit', async function(e){
    e.preventDefault();
    submitBtn.disabled = true;

    const formData = new FormData(form);
    const email = formData.get('email');
    const password = formData.get('password');

    if(!isValidEmail(email)){
        document.getElementById('email').classList.add('is-invalid');
        alert(EMAIL_REQUIREMENTS);
        submitBtn.disabled = false;
        return;
    }

    if(password === ""){
        document.getElementById('password').classList.add('is-invalid');
        submitBtn.disabled = false;
        return;
    }

    const payload = {};
    for(const [key, value] of formData.entries()){
        payload[key] = value;
    }

    const csrfToken = document.getElementById('_csrf') ? document.getElementById('_csrf').value : null;

    try{
        const res = await fetch(form.getAttribute('action') || '/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
        });

        if(!res.ok){
            const text = await res.text();
            alert('Error: ' + (text || res.statusText));
            submitBtn.disabled = false;
            return;
        }

        if(res.ok){
            window.location.href = '/';
        }
    }catch(err){
        console.error(err);
        alert('An error has occurred');
        submitBtn.disabled = false;
    }
});