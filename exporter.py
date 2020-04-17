import requests
import bs4
import time
import os
import json
from utils import Parser
import tomd


pwd = os.path.split(os.path.realpath(__file__))[0]

headers = {'content-type': "application/json",'Referer':"https://baijiahao.baidu.com",
           "User-Agent": 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36',
           'Cookie':"UserName=novelly; UserToken=f456977858b24e95ae11509983de94be;"}


def write_txt(str,file_name):
    with open(file_name, 'w') as f:
        f.write(str)

def getArticle(id):
    url = "https://blog-console-api.csdn.net/v1/editor/getArticle?id={}".format(id)
    print(url)
    response = requests.get(url,headers=headers);
    responseJson = json.loads(response.content)
    if responseJson['data']['markdowncontent'] == '':
        text = tomd.convert(responseJson['data']['content'])
        # parser = Parser(responseJson['data']['content'])
        responseJson['data']["markdowncontent"] = text
        # print(''.join(parser.outputs))
    write_txt(json.dumps(responseJson['data'],ensure_ascii=False),pwd+"/articles/"+id+".txt")
    time.sleep(1)

def getArticleList(name):
    hrefs = set()
    total = 0
    for num in range(1, 100):
        url = "https://blog.csdn.net/{}//article/list/{}".format(name, num)
        print(url)
        response = requests.get(url);
        # 使用BeautifulSoup解析代码,并锁定页码指定标签内容
        content = bs4.BeautifulSoup(response.content.decode("utf-8"), "html.parser")  # 缩进格式
        if content.text == "404":
            break
        items = content.select_one('div[class="article-list"]')
        if not items:
            break;
        for item in items.find_all("a"):
            # article-list
            href = item.get("href")
            if href and href.startswith("https://blog.csdn.net/{}/article/details".format(name)):
                print(href)
                print(href.split("/")[-1])
                hrefs.add(href.split("/")[-1])
        if total == len(hrefs):
            break;
        total = len(hrefs)
        time.sleep(1)
        print(total)
    return hrefs


# https://blog-console-api.csdn.net/v1/editor/getArticle?id=36398755
if __name__ == '__main__':
    print(
        tomd.convert('<tbody><tr><td><code class="highlighter-rouge">Heading level 1<br>===============</code></td><td><code class="highlighter-rouge">&lt;h1&gt;Heading level 1&lt;/h1&gt;</code></td><td><h1 class="no-anchor" data-toc-skip="" id="heading-level-1-1">Heading level 1</h1></td></tr><tr><td><code class="highlighter-rouge">Heading level 2<br>---------------</code></td><td><code class="highlighter-rouge">&lt;h2&gt;Heading level 2&lt;/h2&gt;</code></td><td><h2 class="no-anchor" data-toc-skip="" id="heading-level-2-1">Heading level 2</h2></td></tr></tbody>')
    )