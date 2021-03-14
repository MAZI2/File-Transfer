module.exports = {
    devServer: {
        port: 8081,
        proxy: {

            "/Server": {
                target: "http://localhost:8080",
                secure: false
            },
            "/Client": {
                target: "http://localhost:8080",
                secure: false
            },
            "/update": {
                target: "http://localhost:8080",
                secure: false
            },
            "/ips": {
                target: "http://localhost:8080",
                secure: false
            }
        }
    }
};