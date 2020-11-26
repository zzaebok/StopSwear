from bs4 import BeautifulSoup
import requests
from concurrent.futures import ThreadPoolExecutor

# global variables
dc_url = "https://gall.dcinside.com"
headers = {
    "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36"
}

def get_soup_obj(url):
    req = requests.get(url,headers=headers)
    soup = BeautifulSoup(req.text, "html.parser")
    return soup

def get_urls():
    soup = get_soup_obj(dc_url)
    cate_lists = soup.find("div", attrs={"id": "categ_listwrap"})
    urls = []
    for category_ul in cate_lists.find_all("ul"):
        if "class" in category_ul.attrs:
            continue
        for category in category_ul.find_all("li"):
            if category.find("a"):
                urls.append(category.find("a").attrs["href"])
    return urls

def crawl_board(url):
    soup = get_soup_obj(url)
    titles = soup.find_all("tr", attrs={"class": "ub-content us-post"})
    if len(titles):
        texts = []
        for title in titles:
            number = title.find("td", attrs={"class": "gall_num"}).text
            if number.isdigit():
                try:
                    texts.append(title.find("td", attrs={"class": "gall_tit ub-word"}).find("a").text)
                except AttributeError:
                    continue
        return texts
    else:
        return []
