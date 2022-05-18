import yaml

with open('./config.yaml') as mfile:
    settings = yaml.load(mfile.read(), yaml.FullLoader)


def query_yaml(key):
    data = settings
    for i in key.split('.'):
        data = data[i]
    return data
