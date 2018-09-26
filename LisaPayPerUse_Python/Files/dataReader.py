import pandas as pd

#Load data
df_products=pd.read_csv("../Data/produtos.txt",sep='|',encoding='ANSI',dtype=str)
df_historico=pd.read_csv("../Data/historico.txt",sep='|',encoding='ANSI',dtype=str)
df_clientes=pd.read_csv("../Data/clientes.txt",sep='|',encoding='ANSI',dtype=str)

#Set columns to proper types
df_products['PRICE'] = df_products['PRICE'].astype(float)
df_products['RENT_PRICE'] = df_products['RENT_PRICE'].astype(float)

df_historico['QUANTITY']=df_historico['QUANTITY'].astype(float)
df_historico['DISCOUNT']=df_historico['DISCOUNT'].astype(float)

#Criar tabela toda com historico do Manuel
df_historico_completo=df_historico.merge(df_products,on=['PRODUCT_ID'],how='left')

#Possible Statistics

