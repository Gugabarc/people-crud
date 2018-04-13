Projeto realizado para processo seletivo em 2018.

##Problema:

Instruções:
Desenvolver uma aplicação web de cadastro de Pessoa, que pode ser física ou jurídica.
Uma pessoa física pode ter um ou mais filhos. A aplicação deve salvar as informações no
banco de dados.
Os seguintes campos são obrigatórios no cadastro, e a nomenclatura varia conforme o tipo
de pessoa:
- Nome / Razão social
- Município
- UF
- Data de Nascimento / Fundação

1. Deverá ter as operações CRUD.

2. Na tela de listagem, deverá haver um botão Gerar relatórios, que exporte um
relatório pdf, com todas pessoas agrupados da seguinte forma:
a. Pessoas Físicas
i. Dependentes
b. Pessoas Jurídicas

3. Dentro do componente para dependentes, deve ser exibido o nome e a data de
nascimento.
Entregáveis:
- Scripts para banco de dados
- Código Fonte
- Aplicação web funcionando
- Roteiro caso seja necessário alguma lib para o servidor, externa ao maven.
Tecnologias necessárias:
- Primefaces
- EJB
- Maven
- WildFly
- JPA
- JUnit
- Mockito
- JasperReport


##Solução Adotada

Devido a complexidade do setup do ambiente, e o curto prazo de desenvolvimento (foi estabelecido um prazo inicial de 24h para conclusão, e posteriormente, conforme combinado anteriormente, solicitei algumas horas a mais), utilizei o framework de criação de projetos JBoss Forge. Nesta primeira etapa houve uma pequena curva de aprendizado, pois pouco havia utilizado esta tecnologia até então. Surgiram dúvidas relacionadas a nomenclatura de classes e atributos, que inicialmente seriam em inglês. Devido à experiências anteriores com ERPs, e para evitar nomes que forçam uma tradução de termos que são mais claros em nosso idioma nativo, adotou-se o português no nome das entidades que representam Dependentes e Pessoas Física e Jurídica. Também foi necessário realizar download do JBoss Wildfly 11, e o configurar para executar a aplicação.

Definida a estrutura base do projeto, realizei ajustes nas telas, e as traduzi do inglês para português. Na etapa seguinte optei por utilizar o banco PostgreSQL (versão 10.3), pois seu setup inicial no ambiente de desenvolvimento teria menor custo, e a partir disto foi configurado o datasource para acesso ao banco. Aqui foram enfrentados alguns problemas, pois o JBoss não conseguia acessar o banco com o último driver jdbc disponível, a solução encontrada foi a utilização de uma versão anterior a atual.

Após definido o script SQL de setup do banco, e realizadas adaptações na interface, acrescentei suporte ao JasperReports, tecnologia que trabalhei entre 2009 e 2014. A primeira versão foi a geração de um report simples através de um método main, sendo posteriormente adaptado para um servlet. Aqui foi necessário o setup inicial do Jasper Studio, ferramenta para desenvolvimento do layout do relatório, e resgatar conhecimentos de anos atrás relacionados a ferramenta (na época chamada de iReport) e o uso adequado de relatórios e subrelatórios. A necessidade de uso de um subrelatório surgiu diante do requisito de incluir no relatório os dependentes de cada pessoa física cadastrada. Foram acrescentados logs para simples monitoramente de performance. Um exemplo do relatório gerado pelo sistema pode ser visualizado aqui: https://github.com/Gugabarc/people-crud/blob/master/pessoasReportExample.pdf

Devido principalmente ao tempo disponível, ao prazo fornecido, e a ausência de regras de negócio, ainda que possível validar alguns comportamentos da aplicação, não foram acrescentados testes unitários. Como o uso de JUnit, AssertJ, Mockito, PowerMock e outros frameworks que auxiliam o desenvolvimento de testes, são indispensáveis em um projeto com um mínimo de qualidade, tenho disponível um sistema simples desenvolvido em 2016 para outro processo seletivo, onde algumas destas tecnologias foram utilizadas. O que faria de diferente na parte de testes deste projeto seria a inclusão de DataPoints, com o intuito de definir um input de dados a serem validados e reduzir o tamanho do código. Também incluiria o framework AssertJ para melhorar a legibilidade. Link do projeto: https://github.com/Gugabarc/theater-tickets

##Itens futuros
- Incluir data de nascimento nas informações do dependente que são exibidas dentro do cadastro de Pessoa Física
- Agrupar as pessoas por tipo (PJ/PF) no relatório, ou dividir em dois relatórios (um para PJ e outro para PF)
- Mover usuário e senha do banco para um arquivo de properties, e colocá-lo fora da aplicação, assim evitando que o desenvolvedor tenha acesso a dados de produção
- Acrescentar Spring Data e desacoplar as DAOs dos EJBs, e já refatorando/avaliando o código gerado pelo JBoss Forge
- Acrescentar mais logs, pois até o momento foi adicionado apenas na geração de relatórios
- Alterar o componente de input de datas na interface, para um de melhor usabilidade
- Ajustes na interface do layout, e talvez reestruturar a forma como os dependentes são listados
- Ao não existir dados no banco de dados, o relatório deve ser impresso vazio respeitando seu layout original, atualmente está gerando um PDF em branco
- Realizar mais testes no relatório para verificar possíveis deformações com diferentes cargas de dados
