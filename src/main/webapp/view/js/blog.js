const getElement = (id) => document.getElementById(id);
const AUTHOR_NAME = '黃翊程';//預設作者姓名(正常應該透過Cookie和Session取得，但要寫帳號密碼登入系統)
const showMessage = (text, isSuccess = false) => //共用的提示彈窗方法
{
    getElement('MessageText').textContent = text;
    const icon = getElement('MessageIcon');
    if (isSuccess)
	{
        icon.className = 'fa-solid fa-circle-check message-icon';
        icon.style.color = '#00f5d4'; // 成功用青色
    }
	else
	{
        icon.className = 'fa-solid fa-circle-exclamation message-icon';
        icon.style.color = '#f87171'; // 警告用紅色
    }
    getElement('MessageModal').classList.add('active');
};
window.onload = () =>//網頁加載完成後的初始化
{
    getElement('btnPublish').onclick = publishArticle;//發布按鈕
	getElement('SaveEditActions').onclick = saveArticle;//保存按鈕
    getElement('CancelEditActions').onclick = closeEditModal;//取消保存按鈕
	getElement('EditArticleCloseWindow').onclick = closeEditModal;//叉叉關閉按鈕
	getElement('EditArticleModal').addEventListener('click', (e) => //點擊空內容關閉視窗
	{
        if (e.target === getElement('EditArticleModal'))
		{
            closeEditModal();
        }
    });
	getElement('CancelDeleteActions').onclick = closeDeleteModal;//取消刪除按鈕
    getElement('ConfirmDeleteActions').onclick = executeDelete;//確認刪除按鈕
    getElement('DeleteArticleModal').addEventListener('click', (e) => //點擊空內容關閉視窗
	{
        if (e.target === getElement('DeleteArticleModal')) closeDeleteModal();
    });
	getElement('articleList').addEventListener('click', (e) => //文章的刪除和編輯
	{
        const btn = e.target.closest('button');
        if (!btn) return;
		const id = btn.dataset.id;
		if (btn.closest('.article-delete-container'))
		{
            openDeleteModal(id);
        }
		else if (btn.closest('.article-edit-container'))
		{
            openEditModal(id);
        }		
    });
	getElement('IntroductionEditing').onclick = openIntroductionModal; //點擊「編輯簡介」打開彈窗
    getElement('CancelIntroductionEditActions').onclick = closeIntroductionModal;//取消編輯簡介視窗
    getElement('SaveIntroductionEditActions').onclick = savePersonalInformation;//儲存簡介
    getElement('EditIntroductionCloseWindow').onclick = closeIntroductionModal;//叉叉關閉編輯簡介視窗
	getElement('EditIntroductionModal').addEventListener('click', (e) => //點擊黑色遮罩關閉個人簡介彈窗
	{
        if (e.target === getElement('EditIntroductionModal'))
		{
            closeIntroductionModal();
        }
    });
	getElement('ConfirmMessageActions').onclick = () =>
	{
        getElement('MessageModal').classList.remove('active');
    };    
    getElement('MessageModal').addEventListener('click', (e) => //點擊遮罩也可以關閉
	{
        if (e.target === getElement('MessageModal'))
		{
            getElement('MessageModal').classList.remove('active');
        }
    });
	getElement('LoginToggle').onclick = async () =>
	{
		if (isLoggedIn)
		{
            await logout();
        } 
		else
		{
            getElement('LoginModal').classList.add('active');
        }
    };
    getElement('SubmitLogin').onclick = submitLogin;
	getElement('AdministratorLoginCloseWindow').onclick = closeLoginModal; //叉叉關閉登入視窗
	getElement('LoginModal').addEventListener('click', (e) => //點擊遮罩也可以關閉
	{
        if (e.target === getElement('LoginModal'))
		{
            getElement('LoginModal').classList.remove('active');
        }
    });
	checkLoginStatus();
	loadPersonalInformation();//撈個人資訊
    loadArticles();//第一次開網頁，自動去撈文章
};