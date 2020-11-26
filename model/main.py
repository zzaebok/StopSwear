import crawl
from models.dict import Dict
from models.conv import Conv
from concurrent.futures import ThreadPoolExecutor
import time

if __name__ == '__main__':
    start_time = time.time()
    urls = crawl.get_urls()
    print('number of boards in DC: ', len(urls))

    results = []
    for url in urls:
        results.append(crawl.crawl_board(url))

    with ThreadPoolExecutor(max_workers=16) as executor:
        results = executor.map(crawl.crawl_board, urls)
    results = list(results)

    # array of array to array
    flatten_results = []
    for result in results:
        flatten_results.extend(result)
    print('number of texts crawled: ', len(flatten_results))

    # decide model
    # model = Dict()
    model = Conv()
    model.classify(flatten_results)

    print(time.time() - start_time)
    