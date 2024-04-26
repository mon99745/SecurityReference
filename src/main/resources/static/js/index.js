function getToken() {
    // 로컬 스토리지에서 JWT 토큰을 가져옴
    var accessToken = localStorage.getItem("accessToken");
    var refreshToken = localStorage.getItem("refreshToken");

    // JWT 토큰 값이 없다면 경고를 표시하고 로그인 페이지로 이동
    if (!accessToken || !refreshToken) {
        console.log("JWT 토큰이 존재하지 않습니다.");
        window.location.href = "/login";
        return;
    } else {
        return {
            accessToken: accessToken,
            refreshToken: refreshToken
        };
    }
}

function goPage(url) {
    var tokens = getToken();

    var accessToken = tokens.accessToken;
    var refreshToken = tokens.refreshToken;

    fetch(url, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + accessToken
            // "refreshToken": "Bearer " + refreshToken
        }
    })
        .then(response => {
            if (response.ok) {
                return response.text(); // 성공적인 응답을 텍스트로 반환
            }
            throw new Error("Network response was not ok.");
        })
        .then(html => {
            // 서버로부터 받은 HTML 응답을 이용하여 페이지 이동
            document.open();
            document.write(html);
            document.close();
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function login() {
    document.getElementById("loginForm").addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 동작 방지

        var formData = new FormData(this);

        fetch("/user/login", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);

                window.location.href = "/";
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });
}

function logout() {
    var tokens = getToken();

    var accessToken = tokens.accessToken;
    var refreshToken = tokens.refreshToken;

    fetch("/user/logout", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + accessToken
            // "refreshToken": "Bearer " + refreshToken
        }
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/login-page";
            } else {
                throw new Error("Network response was not ok.");
            }
        })
        .catch(error => {
            // 에러 처리
            console.error("Error:", error);
        });
}

function submitForm() {
    var formData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        roles: [document.getElementById("roles").value]
    };

    fetch("/user/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                window.history.back();
                console.log("회원가입이 완료되었습니다.");
            } else {
                throw new Error("Network response was not ok.");
            }
        })
        .catch(error => {
            // 에러 처리
            console.error("Error:", error);
        });
}

function withdraw() {
    var tokens = getToken();

    var accessToken = tokens.accessToken;
    var refreshToken = tokens.refreshToken;

    fetch("/user/withdraw", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + accessToken
            // "refreshToken": "Bearer " + refreshToken
        }
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/login-page";
            } else {
                throw new Error("Network response was not ok.");
            }
        })
        .catch(error => {
            // 에러 처리
            console.error("Error:", error);
        });
}