FROM nginx:latest
COPY --chown=root:root ./tagpost-ui /usr/share/nginx/html
COPY --chown=root:root ./nginx_conf /opt/bootstrap
ENV TAGPOST_GRPC_WEB_HOST localhost
ENV TAGPOST_GRPC_WEB_PORT 46764
CMD /opt/bootstrap/start_nginx.sh
