
function obtainJson() {
    let inputTerm = document.getElementById("inputTerm").value
    fetch('/api/term/dissect', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            term: inputTerm
        })
    }).then(res => {
        return res.json()
    }).then(data => console.log(data))
}