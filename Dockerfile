FROM tensorflow/tensorflow:2.3.0-gpu-jupyter

MAINTAINER jaebok123@yonsei.ac.kr

COPY requirements.txt /tf

RUN pip install -r /tf/requirements.txt

CMD ["bash" "-c" "source /etc/bash.bashrc && jupyter notebook --notebook-dir=/tf --ip 0.0.0.0 --no-browser --allow-root"]
