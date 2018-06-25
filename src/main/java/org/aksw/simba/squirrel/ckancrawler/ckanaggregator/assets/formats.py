RDF_PDEU = eval("""[u'api/sparql', u'n-triple', u'bz2:nt', u'example/rdfa', u'sparql', u'sparql web form', u'ttl', u'application/rdf xml', u'turtle', u'text/turtle', u'html+rdfa', u'rdf/xml, marcxml', u"'application/rdf xml'", u'rdf/turtle', u'n-triples', u'linked data api, rdf json', u'application/rdf+xml', u'text/ntriples', u'rdfa', u'owl', u'RDF', u'example/rdf+xml', u'meta/void', u'skos rdf', u'n3']""") 
RDF_DATAHUBIO = eval("""[u'data file in excel and rdf', u'html+rdfa ', u'void', u'meta/void', u'rdf endpoint', u'api/sparql', u'application/x-ntriples', u'mapping/<owl>', u'rdf, nt', u'ttl:e1:csv', u'gzip:ntriples', u'meta/rdf-schema ', u'api/sparql ', u'html, rdf', u'gz:ttl:owl', u'nt:transparency-international-corruption-perceptions-index', u'example/owl', u'example/ntriples', u'example/rdf xml', u'bz2:nt', u'api/linked-data', u'example/rdfa', u'sparql', u'rdf, sparql+xml', u'example/rdf+json', u'example/rdf+json ', u'ttl', u'xhtml+rdfa', u'application/x-nquads', u'xml, rdf+xml, turtle and json', u'example/n3', u'example/turtle', u'rdf/n3', u'rdf/xml, html, json', u'application/rdf xml', u'turtle', u'text/turtle', u'text/n3', u'mapping/rdfs', u'html+rdfa', u'example/x-turtle', u'rdf+xml ', u'rdf, csv, xml', u'rdf/nt', u'compressed tarfile containing n-triples', u'rdf-n3', u'rdf/turtle', u'rdf (gzipped)', u'example/n3 ', u'sparql-xml', u'7z:ttl', u'text/rdf+n3', u'n-triples', u'gz:nt', u'gz:nq', u'application/rdf+json', u'application/rdfs', u'ttl.bzip2', u'rdf-turtle', u'gz:ttl', u'example/html+rdfa', u'meta/rdf-schema', u'example/rdf+xml', u'mapping/d2r', u'application/rdf+xml ', u'application/rdf+xml', u'meta/rdf+schema', u'text/ntriples', u'rdf-xml', u'gzip::nquads', u'application/x-turtle', u'meta/void\t', u'nt:meta', u'api/dcat', u'rdfa', u'ontology', u'example/rdf+n3', u'rdf+xml', u'application/turtle', u'application/n-triples', u'html, rdf, dcif', u'rdf, owl', u'RDF', u'application/ld+json', u'json-ld', u'example/rdf+ttl', u'xhtml, rdf/xml, turtle', u'nt', u'rdf:products:org:openfoodfacts', u'sparql-json', u'rdf/xml, turtle, html', u'mapping/owl', u'n3']""")
RDF_DATAGOV = eval("""[u'RDF', u'applicatoin/xml+rdf', u'application/rdf+xml']""")
RDF = set(RDF_PDEU + RDF_DATAHUBIO + RDF_DATAGOV)

#The formats from the above portals are clustered down below
RDF_N3 = eval("""[u'n3', u'example/n3', u'rdf/n3', u'text/n3', u'rdf-n3', u'example/n3 ', u'text/rdf+n3']""")
RDF_TTL = eval("""[u'ttl', u'turtle', u'text/turtle', u'rdf/turtle', u'example/turtle', u'turtle', u'gz:ttl:owl', u'example/x-turtle', u'ttl:e1:csv', u'rdf/turtle', u'7z:ttl', u'rdf-turtle', u'gz:ttl', u'application/x-turtle', u'application/turtle', u'example/rdf+ttl']""")
RDF_NT = eval("""[u'n-triple', u'bz2:nt', u'n-triples', u'text/ntriples', u'application/x-ntriples', u'rdf, nt', u'gzip:ntriples', u'example/ntriples', u'bz2:nt', u'application/x-nquads', u'rdf/nt', u'compressed tarfile containing n-triples', u'n-triples', u'gz:nt', u'gz:nq', u'text/ntriples', u'gzip::nquads', u'application/n-triples', u'nt',]""")
RDF_XML = eval("""[u'application/rdf xml', u'rdf/xml, marcxml', u"'application/rdf xml'", u'application/rdf+xml', u'owl', u'RDF', u'example/rdf+xml', u'meta/void', u'skos rdf', u'data file in excel and rdf', u'void', u'meta/void', u'meta/rdf-schema ', u'example/owl', u'example/rdf xml', u'rdf, sparql+xml', u'xml, rdf+xml, turtle and json', u'rdf/xml, html, json', u'application/rdf xml', u'rdf+xml ', u'rdf, csv, xml', u'rdf (gzipped)', u'sparql-xml', u'meta/rdf-schema', u'example/rdf+xml', u'application/rdf+xml ', u'application/rdf+xml', u'meta/rdf+schema', u'rdf-xml', u'meta/void\t', u'ontology', u'rdf+xml', u'html, rdf, dcif', u'rdf, owl', u'RDF', u'xhtml, rdf/xml, turtle', u'rdf/xml, turtle, html', u'RDF', u'applicatoin/xml+rdf', u'application/rdf+xml']""")
RDF_SPARQL = eval("""[u'api/sparql', u'sparql', u'sparql web form', u'linked data api, rdf json', u'rdf endpoint', u'api/sparql', u'api/sparql ', u'api/linked-data', u'sparql', u'rdf, sparql+xml', u'sparql-xml', u'api/dcat', u'sparql-json']""")

#CSV
CSV = eval("""[u' csv', u' shp, gml, geojson, csv', u'1_csv_format / zip', u'CSV', u'bz2:csv', u'cav', u'comma delimited file, zip compression', u'comma separated variable (csv)', u'csv ', u'csv (inside zip)', u'csv (wfs)', u'csv (zip)', u'csv (zipado)', u'csv (zipped)', u'csv / zip', u'csv and xls', u'csv file', u'csv ods pdf', u'csv or kml', u'csv | mdb', u'csv(txt)', u'csv, excel', u'csv, kml', u'csv, kml, xls', u'csv, sas, spss', u'csv, shp', u'csv, xls', u'csv, xls m.fl.', u'csv, xls, ods, pdf mm', u'csv, xls, openoffice, pdf mm', u'csv, xls, pdf', u'csv, xls, prn, dbase med flere', u'csv, xml', u'csv, xml, shapefile, kml/kmz', u'csv, xml, xsd', u'csv,json', u'csv,xml', u'csv-datei', u'csv-semicolon delimited', u'csv-tab delimited', u'csv.zip', u'csv/api', u'csv/sqlite', u'csv/txt, adobe flash', u'csv/txt, kml/kmz', u'csv/txt, pdf, kml/kmz, tsv, xml', u'csv/txt, xls', u'csv/txt, xls, html', u'csv/txt; pdf', u'csv/txt; sgml; xml', u'csv/txt; xml; tiff', u'csv/utf8', u'csv/webservice/api', u'csv:1', u'csw', u'cvs', u'cvs:xml', u'html, gif, pdf, csv', u'html, json, xml, csv, tsv, tag', u'html, json, xml, csv, tsv, tag, kml', u'html, zip, csv, txt', u'http, csv, pdf, xls, kml, shp, ascii grid', u'http, csv, xls', u'http, csv, xls, pdf', u'json / csv / html / rss', u'json, csv', u'mysql,csv', u'netcdf, csv, shapefiles', u'ppt, doc, csv, kmz, jpg, txt', u'rdf, csv, xml', u'scv', u'text/comma-separated-values', u'text/tab', u'text/tab-separated-values', u'text/x-csv', u'text/x-osdata-csv', u'text/x-osmapping-csv', u'tgz|tsv', u'these are comma-delimited text files.', u'tsv', u'tsv, xml, gif, kml, json', u'ttl:e1:csv', u'txt (csv)', u'txt(csv)', u'txt, xml, cvs, tsv', u'txt/cvs', u'vsv', u'xhtml, html, asc, csv, kml,  mat, nc, odv txt, csv, tsv, json, xhtml, das, dds, dods ', u'xls / csv', u'xls en csv', u'xls, csv', u'xml, csv, excel', u'xml, csv, html', u'xml, csv, pdf, jpg, doc', u'xml, csv/txt, shapefile, kml/kmz, pdf', u'xml, csv/txt, xls, shapefile, kml/kmz, pdf', u'zip (csv utf8)', u'zip (csv)', u'zip (xml, csv/txt)', u'zip file containing multiple csv files.', u'zip, csv, shp', u'zip:csv', u'zip:xml en csv']""")