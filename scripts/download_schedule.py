import os
import requests
import shutil

from sys import argv
from bs4 import BeautifulSoup


def download(_url, _filename):
    r = requests.get(_url, stream=True)
    if r.status_code == 200:
        with open(_filename, 'wb') as f:
            r.raw.decode_content = True
            shutil.copyfileobj(r.raw, f)
            print(_url)
    else:
        print(_url, '- error')


def create_dir(name):
    if not os.path.exists(name):
        os.mkdir(name)


def main():
    site = 'https://www.mirea.ru/schedule/'
    cur_dir = os.getcwd()
    schedule_dir = cur_dir + '/schedule/'
    html_file = cur_dir + '/temp.html'
    download(site, html_file)
    create_dir(schedule_dir)
    with open(html_file, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'lxml')
        html = soup.prettify()
        html_lines = html.split('\n')
        for line in html_lines:
            if '<a class="uk-link-toggle" href="http://webservices.mirea.ru/upload/iblock/' in line:
                start = line.find('http://webservices.mirea.ru/upload/iblock/')
                end = line.find('.xlsx') + 5
                url = line[start:end:]
                name_start = line.rindex('/') + 1
                try:
                    name_end = line.index('.xlsx') + 5
                except ValueError:
                    name_end = line.index('.xls') + 4
                name = line[name_start:name_end:]
                filename = schedule_dir + '/' + name
                print(url)
                download(url, filename)
    os.remove(html_file)


if __name__ == '__main__':
    main()

