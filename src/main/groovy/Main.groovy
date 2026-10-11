import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType
import org.jsoup.Jsoup

class Main {
    static void main(String[] args) {
        def urlInicial = 'https://www.gov.br/ans/pt-br'
        def resposta = buscarPagina(urlInicial, 'página inicial da ANS')
        def documento = Jsoup.parse(resposta, 'https://www.gov.br/ans/pt-br')
        println "Título da página: ${documento.title()}"

        // localiza o link do Espaço do Prestador de Serviços de Saúde
        def linkPrestadores = localizarLink(documento, 'espaço do prestador de serviços de saúde')

        if (linkPrestadores == null) {
            println 'Link dos prestadores não encontrado.'
            return
        }

        def urlPrestadores = linkPrestadores.absUrl('href')
        println "\nLink dos prestadores encontrado:"
        println urlPrestadores

        // acessa a página dos prestadores
        def httpPrestadores = new HTTPBuilder(urlPrestadores)
        def htmlPrestadores = httpPrestadores.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP dos prestadores: ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException("Erro HTTP ao acessar os prestadores: ${resp.status}")
            }
        }

        // interpreta o HTML da página dos prestadores
        def paginaPrestadores = Jsoup.parse(htmlPrestadores, urlPrestadores)
        println "\nTítulo da página dos prestadores:"
        println paginaPrestadores.title()

        // localiza os links relacionados ao TISS
        def linksTISS = paginaPrestadores.select('a[href]').findAll { link ->
            link.text().toLowerCase().contains('tiss')
        }

        if (linksTISS.isEmpty()) {
            println '\nNenhum link relacionado ao TISS foi encontrado.'
            return
        }

        // localiza o link da página principal do Padrão TISS
        def linkTISS = localizarLink(linksTISS, 'padrão para troca de informação de saúde suplementar')

        if (linkTISS == null) {
            println 'Link da página principal do Padrão TISS não encontrado.'
            return
        }

        // obtém a URL da página do Padrão TISS
        def urlTISS = linkTISS.absUrl('href')
        println "\nAcessando a página do Padrão TISS:"
        println urlTISS

        def httpTISS = new HTTPBuilder(urlTISS)

        def htmlTISS = httpTISS.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP do Padrão TISS: ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException("Erro HTTP ao acessar o Padrão TISS: ${resp.status}")
            }
        }

        def paginaTISS = Jsoup.parse(htmlTISS, urlTISS)
        println "\nTítulo da página do Padrão TISS:"
        println paginaTISS.title()

        // localiza o link da versão mais recente do Padrão TISS
        def linkVersao = localizarLink(paginaTISS, 'acessar a versão')

        if (linkVersao == null) {
            println 'Link da versão mais recente não encontrado.'
            return
        }

        def urlVersao = linkVersao.absUrl('href')
        println "\nLink da versão mais recente:"
        println linkVersao.text().trim()
        println urlVersao

        // localiza o link do histórico das versões
        def linkHistorico = localizarLink(paginaTISS, 'acessar todas as versões')

        if (linkHistorico == null) {
            println 'Link do histórico não encontrado.'
            return
        }

        def urlHistorico = linkHistorico.absUrl('href')
        println "\nLink do histórico das versões:"
        println linkHistorico.text().trim()
        println urlHistorico

        // localiza o link das tabelas relacionadas
        def linkTabelas = localizarLink(paginaTISS, 'acessar as planilhas')

        if (linkTabelas == null) {
            println 'Link das tabelas relacionadas não encontrado.'
            return
        }

        def urlTabelas = linkTabelas.absUrl('href')
        println "\nLink das tabelas relacionadas:"
        println linkTabelas.text().trim()
        println urlTabelas

        // acessa a página da versão mais recente do Padrão TISS
        def httpVersao = new HTTPBuilder(urlVersao)

        def htmlVersao = httpVersao.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP da versão mais recente: ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException(
                        "Erro HTTP ao acessar a versão mais recente: ${resp.status}"
                )
            }
        }

        def paginaVersao = Jsoup.parse(htmlVersao, urlVersao)
        println "\nTítulo da página da versão mais recente:"
        println paginaVersao.title()

        def tabelaDocumentos = paginaVersao.select('table').find { tabela ->
            tabela.select('tr').any { linha ->
                linha.select('th, td').size() >= 3 &&
                        linha.select('th, td')[0].text().toLowerCase().contains('componente de comunicação')
            }
        }

        if (tabelaDocumentos == null) {
            println '\nTabela de documentos do Padrão TISS não encontrada.'
            return
        }

        def linhaComunicacao = tabelaDocumentos.select('tr').find { linha ->
            def colunas = linha.select('th, td')
            colunas.size() >= 3 &&
                    colunas[0].text().toLowerCase().contains('componente de comunicação')
        }

        if (linhaComunicacao == null) {
            println '\nLinha do Componente de Comunicação não encontrada.'
            return
        }

        println "\nLinha do Componente de Comunicação encontrada:"
        println linhaComunicacao.text()

        def linkComunicacao = localizarLink(linhaComunicacao, 'componente de comunicação')

        if (linkComunicacao == null) {
            println '\nLink para baixar o Componente de Comunicação não encontrado.'
            return
        }

        def urlComunicacao = linkComunicacao.absUrl('href')
        println "\nLink do Componente de Comunicação:"
        println linkComunicacao.text().trim()
        println urlComunicacao

        def diretorioDownloads = new File('./Downloads/Arquivos_padrao_TISS')

        if (!diretorioDownloads.exists()) {
            diretorioDownloads.mkdirs()
        }

        def nomeArquivo = urlComunicacao.substring(urlComunicacao.lastIndexOf('/') + 1)
        def arquivoDestino = new File(diretorioDownloads, nomeArquivo)
        baixarArquivo(urlComunicacao, arquivoDestino, 'Componente de Comunicação')

        def httpHistorico = new HTTPBuilder(urlHistorico)

        def htmlHistorico = httpHistorico.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP do histórico: ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException(
                        "Erro HTTP ao acessar o histórico: ${resp.status}"
                )
            }
        }

        def paginaHistorico = Jsoup.parse(htmlHistorico, urlHistorico)
        println "\nTítulo da página do histórico:"
        println paginaHistorico.title()

        def tabelaHistorico = paginaHistorico.select('table').find { tabela ->
            def cabecalho = tabela.select('tr')
                    .take(3)
                    .collect { it.text().toLowerCase() }
                    .join(' ')

            cabecalho.contains('competência') && cabecalho.contains('publicação') && cabecalho.contains('início de vigência')
        }

        if (tabelaHistorico == null) {
            println '\nTabela do histórico não encontrada.'
            return
        }

        def historico = coletarHistorico(tabelaHistorico)

        println "\nTotal de registros coletados: ${historico.size()}"

        def arquivoHistorico = new File(diretorioDownloads, 'historico_versoes.csv')
        salvarHistoricoCSV(historico, arquivoHistorico)

        println "\nHistórico salvo em:"
        println arquivoHistorico.absolutePath
        println "Tamanho: ${arquivoHistorico.length()} bytes"

        def httpTabelas = new HTTPBuilder(urlTabelas)

        def htmlTabelas = httpTabelas.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP das tabelas relacionadas: ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException("Erro HTTP ao acessar as tabelas relacionadas: ${resp.status}")
            }
        }

        def paginaTabelas = Jsoup.parse(htmlTabelas, urlTabelas)
        println "\nTítulo da página das tabelas relacionadas:"
        println paginaTabelas.title()

        def linkErros = localizarLink(paginaTabelas, 'erros no envio para a ans')

        if (linkErros == null) {
            println '\nLink da tabela de erros no envio para a ANS não encontrado.'
            println '\nLinks encontrados na página:\n'

            paginaTabelas.select('a[href]').each { link ->
                println "Texto: ${link.text().trim()}"
                println "URL: ${link.absUrl('href')}"
                println "----------------------------------------"
            }
            return
        }

        def urlErros = linkErros.absUrl('href')
        println "\nLink da tabela de erros encontrado:"
        println linkErros.text().trim()
        println urlErros

        if (!diretorioDownloads.exists()) {
            diretorioDownloads.mkdirs()
        }

        def nomeArquivoErros = urlErros.substring(urlErros.lastIndexOf('/') + 1)
        def arquivoErros = new File(diretorioDownloads, nomeArquivoErros)
        baixarArquivo(urlErros, arquivoErros, 'tabela de erros')
    }

    static String buscarPagina(String url, String descricao) {
        def http = new HTTPBuilder(url)

        return http.request(Method.GET, ContentType.TEXT) {
            response.success = { resp, reader ->
                println "\nStatus HTTP (${descricao}): ${resp.status}"
                return reader.text
            }
            response.failure = { resp ->
                throw new RuntimeException(
                        "Erro HTTP ao acessar ${descricao}: ${resp.status}"
                )
            }
        }
    }

    static void baixarArquivo(String url, File arquivoDestino, String descricao) {
        arquivoDestino.parentFile?.mkdirs()
        def http = new HTTPBuilder(url)

        http.request(Method.GET) { req ->
            response.success = { resp, inputStream ->
                arquivoDestino.withOutputStream { outputStream ->
                    outputStream << inputStream
                }
                println "\nDownload de ${descricao} concluído!"
                println "Arquivo salvo em: ${arquivoDestino.absolutePath}"
                println "Tamanho: ${arquivoDestino.length()} bytes"
            }
            response.failure = { resp ->
                throw new RuntimeException(
                        "Erro ao baixar ${descricao}: HTTP ${resp.status}"
                )
            }
        }
    }

    static def localizarLink(def raiz, String textoProcurado) {
        def links = raiz instanceof Collection ? raiz : raiz.select('a[href]')

        return links.find { link ->
            link.text().trim().toLowerCase().contains(textoProcurado.trim().toLowerCase())
        }
    }

    static List coletarHistorico(def tabelaHistorico) {
        def historico = []

        tabelaHistorico.select('tr').each { linha ->
            def colunas = linha.select('th, td')

            if (colunas.size() < 3) {return}

            def competencia = colunas[0].text().trim()
            def publicacao = colunas[1].text().trim()
            def inicioVigencia = colunas[2].text().trim()
            def resultado = competencia =~ /(?i)^(jan|fev|mar|abr|mai|jun|jul|ago|set|out|nov|dez)\/(\d{4})$/

            if (resultado.matches()) {
                def ano = resultado[0][2].toInteger()
                def mes = resultado[0][1].toLowerCase()

                if (ano > 2016 || (ano == 2016 && mes in [
                        'jan', 'fev', 'mar', 'abr', 'mai', 'jun',
                        'jul', 'ago', 'set', 'out', 'nov', 'dez'
                ])) {
                    historico.add([competencia, publicacao, inicioVigencia])
                }
            }
        }
        return historico
    }

    static void salvarHistoricoCSV(List historico, File arquivoHistorico) {
        arquivoHistorico.withWriter('UTF-8') { writer ->
            writer.writeLine('Competência,Publicação,Início de vigência')

            historico.each { registro ->
                def colunasCSV = registro.collect { valor ->
                    '"' + valor.toString().replace('"', '""') + '"'
                }
                writer.writeLine(colunasCSV.join(','))
            }
        }
    }
}