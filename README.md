# BraVE Server

This system provides API endpoint for sharing variant data with [BraVE - BIPMed Variant Explorer](https://bipmed.org/brave).
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
docker network create brave_net

docker container run \
   --rm \
   --detach \
   --name brave_db \
   --network brave_net \
   mongo:4

docker container run \
   --rm \
   --network brave_net \
   --publish 8080:8080 \
   -e SPRING_DATA_MONGODB_URI=mongodb://brave_db:27017/brave \
   bipmed/brave-server
```

## Import test data

```bash
jq -c '.[]' test-data.json | while read variant; do
    curl http://localhost:8080/variants -H "Content-type: application/json" -d ${variant}
done
```

## Querying server

```bash
curl http://localhost:8080/search -H "Content-type: application/json" -d '{"snpId":"rs6040355"}'
curl http://localhost:8080/search -H "Content-type: application/json" -d '{"referenceName":"20", "start":1230237}'
curl http://localhost:8080/search -H "Content-type: application/json" -d '{"referenceName":"20", "start":1230237, "end":1234567}'
curl http://localhost:8080/search -H "Content-type: application/json" -d '{"geneSymbol":"DEFB125"}'
```