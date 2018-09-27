import pandas as pd

#Load data
df_products=pd.read_csv("../Data/produtos.txt",sep='|',encoding='ANSI',dtype=str)
df_historico=pd.read_csv("../Data/historico.txt",sep='|',encoding='ANSI',dtype=str)
df_clientes=pd.read_csv("../Data/clientes.txt",sep='|',encoding='ANSI',dtype=str)

#Set columns to proper types
df_products['PRICE'] = df_products['PRICE'].astype(float)

df_historico['QUANTITY']=df_historico['QUANTITY'].astype(float)
df_historico['DISCOUNT']=df_historico['DISCOUNT'].astype(float)

#Criar tabela toda com historico do Manuel
df_historico_completo=df_historico.merge(df_products,on=['PRODUCT_ID'],how='left')
df_historico_completo['TOTAL_MONEY']=df_historico_completo['PRICE']*df_historico_completo['QUANTITY']

#Possible Statistics and Graphs

#Money spent per brand
df_count_brand= df_historico_completo.groupby('BRAND').TOTAL_MONEY.sum()
df_count_brand.plot(x='BRAND',y='TOTAL_MONEY',kind='bar')

#Money spent on food per type
df_food_only=df_historico_completo.loc[((df_historico_completo['TYPE']!='Action Cam') 
                                        & (df_historico_completo['TYPE']!='Consola Gaming')
                                        & (df_historico_completo['TYPE']!='Eletrodoméstico')
                                        & (df_historico_completo['TYPE']!='Acessórios Gaming'))]
df_food_only=df_food_only.dropna(subset=['TYPE'])
df_sum_food= df_food_only.groupby('TYPE').TOTAL_MONEY.sum()
df_sum_food.plot(x='TYPE',y='TOTAL_MONEY',kind='bar')

