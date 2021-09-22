function showDissectedResults(data) {
    console.log(data)
    const affixMap = data.affixMap
    const term = data.term
    const definition = data.definition
    const jsonAffixes = JSON.stringify(affixMap)
    console.log(jsonAffixes)
    const resultsDiv = document.getElementById("results");
    const affixMapElement = document.createElement("p");
    // affixMapElement.innerText = `${data.affixMap.algesi[0].affix}`;
    affixMapElement.innerText = `${JSON.stringify(data)}`;
    resultsDiv.append(affixMapElement);
}

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
    }).then(data => {
        showDissectedResults(data)
    })
}