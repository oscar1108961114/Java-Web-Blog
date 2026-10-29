const PERSONAL_INFORMATION_API_URL = '/部落格/personal_information';
let currentPersonalInformationData = null; //
const renderPersonalInformation = (data) => //渲染個人資訊到左側區塊
{
    if (!data) return;
    currentPersonalInformationData = data;
    if (data.user_name && data.user_name.trim() !== '')
	{
        getElement('BlogName').textContent = data.user_name;
    }
    if (data.user_dream && data.user_dream.trim() !== '')
	{
        getElement('Dream').textContent = data.user_dream;
    }
    const container = getElement('IntroductionList');
    if (!data.introduction || data.introduction.trim() === '')
	{
        container.innerHTML = '<p>這個人很懶，什麼都沒寫。</p>';
        return;
    }
    const lines = data.introduction.split('\n').filter(line => line.trim() !== '');// ⚠️ 這裡不轉義 HTML，因為我們需要保留 <i> 標籤
    container.innerHTML = lines.map(line => `<p>${line}</p>`).join('');
};
const loadPersonalInformation = async () => //從後端獲取個人資訊
{
    try
	{
        const response = await fetch(PERSONAL_INFORMATION_API_URL);
        if (!response.ok) throw new Error("無法取得個人資訊");
        const data = await response.json();
        renderPersonalInformation(data);
    }
	catch (error)
	{
        console.error("載入個人資訊失敗:", error);
        getElement('IntroductionList').innerHTML = `<p style="color:red;">載入失敗</p>`;
    }
};
const openIntroductionModal = () => //打開編輯視窗
{
    if (!currentPersonalInformationData) return;
    getElement('EditName').value = currentPersonalInformationData.user_name || '';
    getElement('EditDream').value = currentPersonalInformationData.user_dream || '';
    getElement('EditIntroductionText').value = currentPersonalInformationData.introduction || '';
    getElement('EditIntroductionModal').classList.add('active');//顯示視窗
};
const closeIntroductionModal = () => //關閉編輯彈窗
{
    getElement('EditIntroductionModal').classList.remove('active');
};
const savePersonalInformation = async () =>
{
    const nameVal = getElement('EditName').value.trim();
    const dreamVal = getElement('EditDream').value.trim();
    const introVal = getElement('EditIntroductionText').value.trim(); 
    const params = new URLSearchParams();//注意PUT請求要把參數放在查詢字串裡
    params.append('id', 1); // 單例表固定 id=1
    params.append('user_name', nameVal);
    params.append('user_dream', dreamVal);
    params.append('introduction', introVal);
    try
	{
        const response = await fetch(`${PERSONAL_INFORMATION_API_URL}?${params.toString()}`,
		{
            method: 'PUT'
        });
        const result = await response.json();
		if (response.status === 401)// 如果被 Filter 攔截
		{ 
		    showMessage("請先登入管理員帳號，才能進行此操作！");
		    return;
		}
        if (response.ok && result.success)
		{
            showMessage(result.message || "更新成功" , true);
            closeIntroductionModal();
            await loadPersonalInformation(); // 重新載入渲染
        }
		else
		{
            showMessage("更新失敗：" + (result.message || '未知錯誤'));
        }
    }
	catch (error)
	{
        showMessage("連線發生錯誤：" + error.message);
    }
};