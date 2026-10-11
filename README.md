# Crawler ANS - Padrão TISS

## Sobre o projeto

O **Crawler ANS** é uma aplicação desenvolvida em Groovy para automatizar a coleta de informações e o download de arquivos disponibilizados no site da Agência Nacional de Saúde Suplementar (ANS), relacionados ao Padrão TISS (Troca de Informações na Saúde Suplementar).

O Padrão TISS estabelece regras para a troca de informações entre operadoras de planos de saúde e prestadores de serviços de saúde.

A aplicação automatiza a navegação pelas páginas da ANS, identifica links para documentos e arquivos de interesse, realiza downloads e organiza informações históricas em um arquivo CSV.

## Funcionalidades

A aplicação realiza as seguintes operações:

### 1. Acesso às páginas da ANS

A aplicação realiza requisições HTTP para acessar as páginas relacionadas ao Padrão TISS.

O conteúdo HTML retornado é utilizado para localizar links e identificar os recursos necessários para as próximas etapas da execução.

### 2. Identificação de links

A aplicação analisa o HTML das páginas utilizando a biblioteca Jsoup para localizar links relacionados aos recursos desejados.

A partir desses links, consegue navegar entre as páginas necessárias para encontrar os arquivos que serão baixados.

### 3. Download do componente de comunicação

O crawler localiza o link para o componente de comunicação disponibilizado pela ANS e realiza o download do arquivo correspondente.

O arquivo é armazenado localmente para que possa ser acessado posteriormente, sem a necessidade de realizar manualmente a navegação pelo site e o download.

### 4. Coleta do histórico de versões do Padrão TISS

A aplicação acessa a página que contém o histórico de versões do Padrão TISS e identifica a tabela com os registros históricos.

Os dados dessa tabela são extraídos e processados para selecionar os registros a partir de janeiro de 2016.

Essa etapa permite organizar as informações históricas disponibilizadas pela ANS para consulta posterior.

### 5. Exportação do histórico para CSV

Após coletar e processar os registros históricos, a aplicação salva os dados em um arquivo CSV.

Esse formato permite abrir as informações em ferramentas como Microsoft Excel e LibreOffice Calc, além de facilitar o processamento dos dados por outras aplicações.

### 6. Download da tabela de erros

O crawler também localiza o recurso relacionado à tabela de erros disponibilizada no contexto do Padrão TISS e realiza o download do arquivo correspondente.

Dessa forma, a aplicação reúne automaticamente diferentes recursos relacionados ao padrão em vez de exigir que cada arquivo seja localizado e baixado manualmente.

## Fluxo de execução

O funcionamento da aplicação pode ser resumido nas seguintes etapas:

1. A aplicação é iniciada pelo método `main`.
2. São realizadas requisições HTTP para acessar as páginas da ANS.
3. O HTML das páginas é analisado para identificar os links relevantes.
4. O componente de comunicação é localizado e baixado.
5. O histórico de versões do Padrão TISS é coletado.
6. Os registros históricos são filtrados a partir de janeiro de 2016.
7. Os dados históricos são exportados para um arquivo CSV.
8. A tabela de erros é localizada e baixada.

Ao final da execução, os arquivos obtidos ficam disponíveis localmente para consulta e utilização posterior.

## Tecnologias utilizadas

- **Groovy:** linguagem utilizada para implementar a lógica da aplicação.
- **HTTPBuilder:** biblioteca utilizada para realizar requisições HTTP e obter o conteúdo das páginas e dos arquivos remotos.
- **Jsoup:** biblioteca utilizada para analisar o HTML das páginas e localizar elementos e links.
- **Gradle:** ferramenta de automação de build e gerenciamento de dependências, caso utilizada na configuração do projeto.

## Pré-requisitos

Para executar a aplicação, é necessário ter:

- Java instalado e configurado no ambiente.
- Groovy e/ou Gradle disponíveis, conforme a configuração utilizada no projeto.
- Acesso à internet para consultar as páginas da ANS e realizar os downloads.
- As dependências declaradas no arquivo de configuração do projeto instaladas corretamente.

## Como executar

### 1. Clonar o repositório

Clone o projeto para sua máquina:

```bash
git clone <URL_DO_REPOSITORIO>
```

Entre na pasta do projeto:

```bash
cd <NOME_DO_PROJETO>
```

### 2. Verificar as dependências

Confira o arquivo `build.gradle` e certifique-se de que as dependências necessárias estão declaradas.

### 3. Executar a aplicação

Se o projeto estiver configurado para execução pelo Gradle, utilize:

```bash
./gradlew run
```

Esse comando pressupõe que o projeto possua o plugin de aplicação configurado e que a classe principal esteja definida corretamente.

Caso o projeto não utilize essa configuração, a execução deverá seguir a forma definida no próprio projeto.

## Resultados

A execução da aplicação tem como objetivo produzir os seguintes resultados:

| Recurso | Descrição |
|---|---|
| Componente de comunicação | Arquivo disponibilizado pela ANS, obtido por download. |
| Histórico de versões | Arquivo CSV contendo os registros históricos coletados e filtrados a partir de janeiro de 2016. |
| Tabela de erros | Arquivo disponibilizado pela ANS, obtido por download. |

Os arquivos são armazenados localmente conforme os caminhos definidos na implementação.

Os nomes e as extensões dos arquivos dependem dos recursos disponibilizados pela ANS e da forma como os downloads são implementados.

## Tratamento de erros

A aplicação utiliza requisições HTTP para acessar os recursos remotos. Por isso, sua execução depende da disponibilidade das páginas e dos arquivos da ANS.

Problemas de conexão, respostas HTTP malsucedidas ou alterações na estrutura das páginas podem impedir a conclusão de alguma etapa.

Como o crawler depende da estrutura HTML utilizada pelo site, mudanças nos elementos ou nos links das páginas também podem exigir ajustes no código.

## Considerações finais

O projeto demonstra a utilização de técnicas de web crawling para automatizar a coleta de informações disponibilizadas publicamente na internet.

A aplicação combina requisições HTTP, análise de HTML, identificação de links, download de arquivos e exportação de dados para CSV, reduzindo a necessidade de realizar manualmente essas operações.

O resultado é uma ferramenta que facilita a obtenção e a organização de recursos relacionados ao Padrão TISS disponibilizados pela ANS.