// 페이지 이동 함수
function redirectToIndexTest() {
    // 로컬 스토리지에서 JWT 토큰을 가져옴
    var accessToken = localStorage.getItem("accessToken");
    var refreshToken = localStorage.getItem("refreshToken");

    // JWT 토큰 값이 없다면 경고를 표시하고 로그인 페이지로 이동
    if (!accessToken || !refreshToken) {
        console.log("JWT 토큰이 존재하지 않습니다.");
        window.location.href = "/login";
        return;
    }

    var xhr = new XMLHttpRequest();

    // 요청 준비
    xhr.open("GET", "/index-test", true);
    // JWT 토큰을 헤더에 추가
    xhr.setRequestHeader("Authorization", "Bearer " + accessToken);
    // xhr.setRequestHeader("refreshToken", "Bearer " + refreshToken);

    // 요청 완료 후의 처리
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log(xhr.responseText);
                // 서버로부터 받은 HTML 응답을 이용하여 페이지 이동
                document.open();
                document.write(xhr.responseText);
                document.close();
            } else {
                // 에러 처리
                console.error("Error:", xhr.statusText);
            }
        }
    };
    // 요청 전송
    xhr.send();
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
                // 서버에서 받은 토큰을 로컬 스토리지에 저장
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);

                // 로그인 후 페이지 이동 또는 다른 작업 수행
                window.location.href = "/";
            })
            .catch(error => {
                console.error("Error:", error);
                // 에러 처리 위치
            });
    });
}

function logout() {
    // 로컬 스토리지에서 JWT 토큰을 가져옴
    var accessToken = localStorage.getItem("accessToken");
    var refreshToken = localStorage.getItem("refreshToken");

    // JWT 토큰 값이 없다면 경고를 표시하고 로그인 페이지로 이동
    if (!accessToken || !refreshToken) {
        alert("JWT 토큰이 없습니다!");
        window.location.href = "/login";
        return;
    }

    var xhr = new XMLHttpRequest();

    // 요청 준비
    xhr.open("POST", "/user/logout", true);
    // JWT 토큰을 헤더에 추가
    xhr.setRequestHeader("Authorization", "Bearer " + accessToken);
    // xhr.setRequestHeader("refreshToken", "Bearer " + refreshToken);

    // 요청 완료 후의 처리
    xhr.onreadystatechange = function () {
        // 로컬 스토리지에서 토큰 삭제
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/login-page";
    };

    // 요청 전송
    xhr.send();
}

function submitForm() {
    var formData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        roles: [document.getElementById("roles").value]
    };

    // AJAX를 사용하여 서버로 데이터 전송
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/user/create", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // 성공적으로 처리된 경우 처리할 내용
            window.history.back();
            console.log("회원가입이 완료되었습니다.");
        }
    };
    xhr.send(JSON.stringify(formData));
}