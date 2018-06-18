import pandas as pd
import numpy as np
a = pd.read_csv('./part-r-00000(10)',header=None,sep='\t')[1:]
a['mean_time'] = a[1].apply(lambda x:x.split(';')[0]).astype(float)
a['all_time']=a[1].apply(lambda x:x.split(';')[1]).astype(float)
a['times'] = a[1].apply(lambda x:x.split(';')[2]).astype(float)
print('平均每人平均每次登录时间',a['mean_time'].mean(),'秒')
print('平均登录时间',a['all_time'].sum()/a['times'].sum(),'秒')
