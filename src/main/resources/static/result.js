function showDissectedResults(data) {
    console.log(data)
    const affixMap = data.affixMap
    const term = data.term
    const definition = data.definition
    const resultsDiv = document.getElementById("results");
    const affixMapElement = document.createElement("p");
    // affixMapElement.innerText = `${data.affixMap.algesi[0].affix}`;
    const keys = Object.keys(affixMap);
    console.log(keys)
    for (let i = 0; i < keys.length; i++) {
        console.log(keys[i])
        let key = keys[i]
        // console.log(affixMap[`${key}`])
        if (affixMap[`${key}`] !== null) {
            for (let j = 0; j < affixMap[`${key}`].length; j++) {
                console.log(affixMap[`${key}`][j].affix)
                console.log(affixMap[`${key}`][j].meaning)
                console.log(affixMap[`${key}`][j].examples)
            }
        } else {
            console.log(key + " is null")
        }
    }
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