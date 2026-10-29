const MEMBER_API_URL = '/部落格/blog_member';
let isLoggedIn = false;
const closeLoginModal = () =>
{
	getElement('LoginModal').classList.remove('active');
}
const checkLoginStatus = async () =>//檢查登入狀態
{
    try
	{
        const res = await fetch(MEMBER_API_URL);
        const data = await res.json();
        isLoggedIn = data.success;
        updateUIBasedOnLogin();
    }
	catch (error)
	{
        console.error("檢查登入失敗", error);
    }
};
const updateUIBasedOnLogin = () => //根據登入狀態更新 UI
{
    const btnToggle = getElement('LoginToggle');
	const body = document.body;
	if (isLoggedIn)//登入狀態
	{
        btnToggle.textContent = '登出';
        btnToggle.classList.replace('login', 'logout');
		body.classList.remove('logged-out');
        body.classList.add('logged-in');       		
    }
	else//登出狀態
	{
        btnToggle.textContent = '管理員登入';
        btnToggle.classList.replace('logout', 'login');
		body.classList.remove('logged-in');
        body.classList.add('logged-out');	
    }
};
const submitLogin = async () => //處理登入提交
{
    const account = getElement('LoginAccount').value.trim();
    const password = getElement('LoginPassword').value.trim();
    if (!account || !password) return showMessage("請輸入帳號密碼");
    const params = new URLSearchParams();
    params.append('account', account);
    params.append('password', password);
    try
	{
        const res = await fetch(MEMBER_API_URL, 
		{ 
			method: 'POST', 
			body: params 			
		});
        const result = await res.json();
        if (result.success)
		{
            showMessage("登入成功", true);
            getElement('LoginModal').classList.remove('active');
            await checkLoginStatus(); //更新狀態
    	}
		else
		{
	       showMessage(result.message || "登入失敗");
	    }	
    }
	catch (error)
	{
        showMessage("連線失敗");
    }
};
const logout = async () => //登出
{
    try
	{
        const res = await fetch(MEMBER_API_URL, { method: 'DELETE' });
        const data = await res.json();
        if (data.success)
		{
            showMessage("已成功登出", true);
            isLoggedIn = false;
            updateUIBasedOnLogin(); // 更新 UI            
            loadArticles(); 
        }
    }
	catch (error)
	{
        showMessage("登出失敗");
    }
};