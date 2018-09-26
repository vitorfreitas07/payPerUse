import pandas as pd

#Load data
df_products=pd.read_csv("../Data/produtos.txt",sep='|',encoding='ANSI',dtype=str)
df_rent=pd.read_csv("../Data/produtos_rent.txt",sep='|',encoding='ANSI',dtype=str)
df_historico=pd.read_csv("../Data/historico_Manuel.txt",sep='|',encoding='ANSI',dtype=str)


#Criar tabela toda com historico do Manuel
df_historico_completo=df_historico.merge(df_products,on=['PRODUCT_ID'],how='left')




