# frontend/Dockerfile
FROM node:20.19.0 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

FROM node:20.19.0
WORKDIR /app
COPY --from=build /app/dist ./dist
RUN npm install -g serve
EXPOSE 4173
CMD ["serve", "-s", "dist", "-l", "4173"]