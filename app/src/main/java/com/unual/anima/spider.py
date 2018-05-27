import operator as op
import requests
import re
from lxml import etree


class anima(object):
    def __init__(self):
        self.headers = {
            "user-agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"}
        self.get_week_anima()

    # 追更动漫
    def get_week_anima(self):
        url = "http://www.dilidili.wang/"
        home_page = requests.get(url=url).content.decode('utf-8')
        # print(home_page)
        etree_html = etree.HTML(home_page)
        print(home_page)
        self.get_oneday(etree_html, "周一", "elmnt-one")
        # self.get_oneday(etree_html, "周二", "elmnt-two")
        # self.get_oneday(etree_html, "周三", "elmnt-three")
        # self.get_oneday(etree_html, "周四", "elmnt-four")
        # self.get_oneday(etree_html, "周五", "elmnt-five")
        # self.get_oneday(etree_html, "周六", "elmnt-six")
        # self.get_oneday(etree_html, "周日", "elmnt-seven")

    # 每一天更新动漫列表
    def get_oneday(self, etree_html, name="周一", class_name="elmnt-one"):
        url_arry = etree_html.xpath(
            '//ul[@class="wrp animate"]/li[@class="%s"]/div[@class="book small"]/a/@href' % class_name)  # 链接数组
        name_arry = etree_html.xpath(
            # '//ul[@class="wrp animate"]/li[@class="%s"]/div[@class="book small"]/a/figure/figcaption/p[1]/text()' % class_name)  # 链接数组
        '//ul[@class="wrp animate"]/li[@class="%s"]/div[@class="book small"]/a/figure/figcaption/p[1]/text()' % class_name)  # 链接数组
        print("%s节目列表：" % name)
        for i in range(len(url_arry)):
            url = "http://www.dilidili.wang%s" % url_arry[i]
            name = name_arry[i]
            print("    %s->%s" % (name_arry[i], url_arry[i]))
            # self.get_one_anima(name, url)

    # 更新具体动漫，每一集
    def get_one_anima(self, name, url):
        anima_page = requests.get(url=url).content.decode('utf-8')
        etree_html = etree.HTML(anima_page)
        url_arry = etree_html.xpath('//div[@class="swiper-slide"]/ul[@class="clear"]/li/a/@href')
        name_arry = etree_html.xpath('//div[@class="swiper-slide"]/ul[@class="clear"]/li/a/em/text()')
        print('%s:' % name)
        for i in range(len(url_arry)):
            anima_name = name_arry[i].replace(name, "")
            anima_url = url_arry[i]
            # print("    %s>%s" % (anima_name, anima_url))
            try:
                self.get_anima_video(anima_name, anima_url)
            except Exception as e:
                print(e)

    def get_anima_video(self, name, url):
        name = name.strip()
        play_page = requests.get(url=url).content.decode('utf-8')
        p = re.compile(r'var sourceUrl =.+;')  # 我们在编译这段正则表达式
        matcher = re.search(p, play_page)  # 在源文本中搜索符合正则表达式的部分
        video = matcher.group(0).replace('var sourceUrl = "', '').replace('";', '')
        print("%s ->%s" % (name, video))
        # print(play_page)


if __name__ == '__main__':
    anima()
