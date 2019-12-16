
# cds-imports-dds

### End Points:
POST /declarations with header `X-EORI-Identifier` 

#### Examples:
```bash
curl -d '{"lrn": "1234"}' -H "X-EORI-Identifier: eori123" -H "Content-Type: application/json" -XPOST http://localhost:9759/declarations
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
