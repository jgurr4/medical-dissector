class Greeting extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let name = 'john';
        return <h1>Hello, {name}</h1>;
    }
}