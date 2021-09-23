function showDissectedResults(data) {
    console.log(data)
    const affixMap = data.affixMap
    const term = data.term
    const definition = data.definition
    const affixMapKeys = Object.keys(affixMap);
    const resultsDiv = document.getElementById("results");
    console.log(affixMapKeys)
    for (let i = 0; i < affixMapKeys.length; i++) {
        const affixMapElement = document.createElement("p");
        console.log(affixMapKeys[i])
        affixMapElement.innerText = `${affixMapKeys[i]}`;
        resultsDiv.append(affixMapElement);
        let key = affixMapKeys[i]
        if (affixMap[`${key}`] !== null) {
            for (let j = 0; j < affixMap[`${key}`].length; j++) {
                const affixMapElement = document.createElement("p");
                console.log(affixMap[`${key}`][j].affix)
                affixMapElement.innerText = affixMap[`${key}`][j].affix;
                resultsDiv.append(affixMapElement);
                const affixMapElement2 = document.createElement("p");
                console.log(affixMap[`${key}`][j].meaning)
                affixMapElement2.innerText = affixMap[`${key}`][j].meaning;
                resultsDiv.append(affixMapElement2);
                const affixMapElement3 = document.createElement("p");
                console.log(affixMap[`${key}`][j].examples)
                affixMapElement3.innerText = affixMap[`${key}`][j].examples;
                resultsDiv.append(affixMapElement3);
            }
        } else {
            console.log(key + " is null")
        }
    }
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