const input = document.querySelector("input")
input.addEventListener("keydown", e => {
    // console.log(e)   //uncomment this if you want to see key codes and names etc...
    clickPress(e)
})

function clickPress(e) {
    if (e.keyCode == 13) {
        obtainJson()
    }
}

function showDissectedResults(data) {
    let resultExists = document.getElementById("results")
    if (resultExists) {
        resultExists.remove()
    }
    const affixMapKeys = Object.keys(data.affixMap)
    const body = document.body
    const div = document.createElement("div")
    const table = document.createElement("table")
    div.id = "results"
    div.className = "results"
    let tableRow = document.createElement("tr")
    let wordPartHeader = document.createElement("th")
    wordPartHeader.innerText = "Word part"
    let affixHeader = document.createElement("th")
    affixHeader.innerText = "Affix"
    let meaningHeader = document.createElement("th")
    meaningHeader.innerText = "Meaning"
    let examplesHeader = document.createElement("th")
    examplesHeader.innerText = "Examples"
    tableRow.append(wordPartHeader, affixHeader, meaningHeader, examplesHeader)
    table.append(tableRow)
    if (affixMapKeys.length > 6) {
        let increaseSizeAmount = affixMapKeys.length - 6
        div.style.height = 17 + increaseSizeAmount + "rem"
    }
    for (let i = 0; i < affixMapKeys.length; i++) {
        let key = affixMapKeys[i]
        if (data.affixMap[`${key}`] !== null) {
            for (let j = 0; j < data.affixMap[`${key}`].length; j++) {
                let wordPart = document.createElement("td")
                if (j > 0) {
                wordPart.innerText = `${affixMapKeys[i]}` + `(${j+1})`
                } else {
                    wordPart.innerText = `${affixMapKeys[i]}`
                }
                let affix = document.createElement("td")
                affix.innerText = data.affixMap[`${key}`][j].affix
                let meaning = document.createElement("td")
                meaning.innerText = data.affixMap[`${key}`][j].meaning
                let examples = document.createElement("td")
                examples.innerText = data.affixMap[`${key}`][j].examples
                tableRow = document.createElement("tr")
                tableRow.append(wordPart, affix, meaning, examples)
                table.append(tableRow)
            }
        } else {
            continue
        }
    }
    div.append(table)
    body.append(div)
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