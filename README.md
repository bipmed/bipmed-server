# BIPMed Server

This system provides API endpoint for sharing variant data with [BIPMed website](https://bipmed.org/).
It accepts `Query` as POST request and returns a list of `Variant`.

Query

- `variantId` - Unique identifier (rs6054257)
- `assemblyId` - Version of reference genomes (GCRh38, `null` for all reference genomes)
- `datasetId` - Dataset id (bipmedExome, `null` for all datasets)
- `referenceName` - Chromosome (20, must be present when `geneSymbol` is `null`)
- `start` - Exact position when `end` is `null`, otherwise is start (include) of range (1000, must be present)
- `end` - End (include) of range (must be present when `start` is not `null`)
- `geneSymbol` - Gene Symbol (SCN1A, it can be combined with `referenceGenome`, `start` and `end`)

Variant

- `variantIds` - List of unique identifiers where available, can be `null` (ID)
- `assemblyId` - Version of reference genome, required
- `datasetId` - Dataset id, required
- `referenceName` - An identifier from the reference genome, required (CHROM)
- `start` - The reference position, with the 1st base having position 1, required (POS)
- `referenceBases` - Reference bases, required (REF)
- `alternateBases` - List of alternate non-reference alleles, required (ALT)
- `geneSymbol` - Gene symbol, can be `null`
- `alleleFrequency` - Allele Frequency, required (AF)
- `sampleCount` - Number of Samples With Data, required (NS)

## Build docker image
   
```bash
mvn install -DskipTests dockerfile:build
```

## Run Docker images locally

```bash
docker network create bipmed

docker volume create bipmed_data

docker container run \
   --rm \
   --detach \
   --name bipmed_db \
   --network bipmed \
   --volume bipmed_data:/data/db \
   --publish 27017:27017 \
   mongo

docker container run \
   --rm \
   --name bipmed_server \
   --publish 8080:8080 \
   --network bipmed \
   -e SPRING_DATA_MONGODB_URI=mongodb://bipmed_db:27017/bipmed \
   welliton/bipmed-server
```

## Import test data

```bash
mongoimport --collection variant --db bipmed --jsonArray --file test-data.json
```

## Querying server

```bash
curl http://localhost:8080/ -H "Content-type: application/json" -d snpId
curl http://localhost:8080/ -H "Content-type: application/json" -d '{"datasetId":"test"}'
curl http://localhost:8080/ -H "Content-type: application/json" -d '{"referenceName":"20", "start":1230237}'
curl http://localhost:8080/ -H "Content-type: application/json" -d '{"referenceName":"20", "start":1230237, "end":1234567}'
curl http://localhost:8080/ -H "Content-type: application/json" -d '{"geneSymbol":"DEFB125"}'
```