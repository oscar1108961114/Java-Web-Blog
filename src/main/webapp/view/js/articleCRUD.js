const API_URL = '/部落格/articles';
let currentArticles = [];//文章編輯緩存
let editingId = null;//進入編輯狀態的是哪一篇預設為空
let deletingId = null;//進入刪除狀態的是那一篇預設為空
marked.setOptions //由於mardown預設換行為兩下enter，並產生<p>，但現在改為按enter一下也可以換行
({
    breaks: true
}); 
const escapeHtml = (str = '') =>
{
    return String(str).replace(/[&<>"']/g, (m) => (
	{
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[m]));
};
const formatDate = (timestamp) => //時間戳（long 毫秒 → 年月日)
{
    if (!timestamp) return '';
    const d = new Date(timestamp);
    if (isNaN(d.getTime())) return '';
    return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
};
const renderArticles = (articles) =>
{
	const listContainer = getElement('articleList');
	
	if (!articles || articles.length === 0)
	{
	    listContainer.innerHTML =
	        '<p style="color:#9ca3af;">目前還沒有文章，快來發表第一篇吧！</p>';
		return;
	}   
}
const loadArticles = async () => //功能1：去後端撈取所有文章並渲染到網頁上
{
    try
	{       
        const response = await fetch(API_URL);//💡 await 會卡在這裡，直到後端把資料回傳，才會執行下一行，完全不用寫 .then()
        if (!response.ok) throw new Error("無法取得文章列表");
        const articles = await response.json();
		currentArticles = articles;//把目前所有的文章寫入緩存，方便之後編輯的操作
        const listContainer = getElement('articleList');
        listContainer.innerHTML = '';// 清空舊畫面		   
        listContainer.innerHTML = articles.map((article, index) => //串接 HTML渲染
        `
            <div class="article" data-article-id="${article.id}">
				<div class="article-edit-container"><button type="button" data-id="${article.id}">編輯</button></div>
				<div class="article-delete-container"><button type="button" data-id="${article.id}">刪除</button></div>
                <h3>${escapeHtml(article.title)}</h3>
				<div class="current-date">${formatDate(article.created_at)}</div>
                <div class="markdown-content">${DOMPurify.sanitize(marked.parse(article.content))}</div> 
                <div class="author">✍️ 作者: ${escapeHtml(article.author)} | 文章編號: #${index + 1} </div>
            </div>
        `).join('');//DOMPurify.sanitize():將可能含有惡意腳本的程式碼字串清除乾淨；marked.parse():將Markdown純文字轉換成 HTML 字串
		hljs.highlightAll();//hljs:highlight.js函式庫的全域變數縮寫							
    }
	catch (error)
	{
        console.error("載入失敗:", error);
        getElement('articleList').innerHTML = `<p style="color:red;">文章載入失敗：${error.message}</p>`;
    }
};
const publishArticle = async () =>//功能2：發送新文章給後端
{
    const title = getElement('ArticleTitle').value.trim();
    const content = getElement('ArticleContent').value.trim();
    if (!title || !content)
	{
        showMessage("標題和內容都要填寫喔！");
        return;
    }
    const formData = new URLSearchParams();//封裝表單資料
    formData.append('title', title);
    formData.append('content', content);
	formData.append('author', AUTHOR_NAME);
    try
	{
        const response = await fetch(API_URL ,
		{
            method: 'POST',
            body: formData
        });
        const result = await response.json();
		if (response.status === 401)// 如果被 Filter 攔截
		{ 
		    showMessage("請先登入管理員帳號，才能進行此操作！");
		    return;
		}
        if (response.ok && result.success)
		{
            showMessage(result.message, true);
            // 清空輸入框
            getElement('ArticleTitle').value = '';
            getElement('ArticleContent').value = '';
            // 💡 重新加載文章列表，畫面上就會立刻跳出新文章！
            await loadArticles();
        }
		else
		{
           showMessage("發表失敗：" + (result.message || '未知錯誤'));
        }
    }
	catch (error)
	{
        showMessage("連線發生錯誤：" + error.message);
    }
};
const openEditModal = (id) => //功能3:打開編輯視窗
{
    const article = currentArticles.find(a => a.id == id);
    if (!article) return;
    editingId = id; // 記錄正在編輯哪篇
    getElement('editArticleTitle').value = article.title;
    getElement('editArticleContent').value = article.content;
    getElement('EditArticleModal').classList.add('active');//顯示編輯視窗
};
const openDeleteModal = (id) => //功能3:打開刪除視窗
{
    deletingId = id; // 記錄要刪除的 ID
    getElement('DeleteArticleModal').classList.add('active'); // 顯示彈窗
};
const saveArticle = async () => //功能4:保存編輯
{
	const title = getElement('editArticleTitle').value.trim();
    const content = getElement('editArticleContent').value.trim();
	if (!title || !content)
	{
       showMessage("標題和內容都要填寫喔！");
       return;
    }
	const params = new URLSearchParams();
    params.append('id', editingId); // 使用編輯中取得的ID
    params.append('title', title);
    params.append('content', content);
	try
	{
        const response = await fetch(`${API_URL}?${params.toString()}`,
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
            showMessage(result.message , true);
			closeEditModal(); // 關閉視窗
            await loadArticles(); // 重新載入所有文章
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
}
const closeEditModal = () => //功能5:關閉編輯彈窗
{
    getElement('EditArticleModal').classList.remove('active');
    editingId = null;
};
const closeDeleteModal = () => //功能5:關閉刪除彈窗
{
    getElement('DeleteArticleModal').classList.remove('active');
    deletingId = null; //清空記錄
};
const executeDelete = async () => //功能6:刪除文章
{
    if (!deletingId) return;
    try
	{        
        const response = await fetch(`${API_URL}?id=${deletingId}`, // ⚠️ id 必须放 query string，因为后端用 req.getParameter("id")
		{
            method: 'DELETE'
        });
        const result = await response.json();
		if (response.status === 401)// 如果被 Filter 攔截
		{ 
		    showMessage("請先登入管理員帳號，才能進行此操作！");
		    return;
		}
        if (response.ok && result.success)
		{
			closeDeleteModal(); //刪除成功後，順手把彈窗關掉
            await loadArticles();
        }
		else
		{
            showMessage('刪除失敗：' + (result.message || '未知錯誤'));
        }
    }
	catch (error)
	{
        showMessage('連線發生錯誤：' + error.message);
    }
};