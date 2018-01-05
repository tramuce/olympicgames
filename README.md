# Jogos Olímpicos Tokyo 2020
Api Rest disponibilizada para a inclusão e consulta de competições dos jogos olímpicos toky 2020.

# Tecnologias Utilizadas
Construido com String Boot, Spring MVC, Spring Data, HSQLDB, Flyway, MAVEN, Java 8 e documentado com Swagger.

# Projeto Arquitetural
Foi Criada uma arquitetura em 3 camadas, Controller, Service e Repository, todas unificadas por um projeto parent-pom.
Tal arquitetura foi definida para permitir que cada módulo possua suas próprias dependências, haja menor acoplação entre eles, possibilitando substituição das camadas mandante-se apenas à assinatura das interfaces.
A geração automática do banco pelo JPA foi desativada, e os scripts escritos manualmente. A ação foi tomada pois faz com que o banco relacional seja desenhado pensando melhor na solução, e evitando relacionamento ciclicos, além de facilitar o versionamento do banco e futura migração dos dados.
Cada serviço possuí uma interface que defien as assinaturas, visando possibilitar a substituição da classe de implementação sem impacto nas dependências que à utilizam.
Foi mantida a responsabilidade dos métodos, sendo criado um repositório e um serviço específico para cada entidade. O serviço de competição cria as outras entidades caso não existam, porém que de fato acessa o repositório para persistência e consulta, é o serviço específico da entidade solicitada.

# Para rodar a aplicação
Executar Maven update, mvn clean install spring-boot:run.
Para testar os endpoins, verificar descrição no arquivo swagger do repositório.
