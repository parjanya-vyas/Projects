class Bit_OT_graph : Crypto_graph {

    Crypto_node ***bit_ot_nodes;

    public:
    	
    void allocate_nodes_memory() {
    	bit_ot_nodes = new Crypto_node**[field_sz];
        for(DAMN_LONG i=0;i<field_sz;i++) {
            bit_ot_nodes[i] = new Crypto_node*[field_sz];
            for(DAMN_LONG j=0;j<field_sz;j++) {
                bit_ot_nodes[i][j] = new Crypto_node[2];
                bit_ot_nodes[i][j][0].random_vars = new DAMN_LONG[num_random_vars];
                bit_ot_nodes[i][j][1].random_vars = new DAMN_LONG[num_random_vars];
            }
        }
	}

    void init_all_nodes() {
        for(int i=0;i<field_sz;i++) {
            for(int j=0;j<field_sz;j++) {
            	DAMN_LONG vals[2] = {i, j};
                init_single_node(&bit_ot_nodes[i][j][0], vals);
                init_single_node(&bit_ot_nodes[i][j][1], vals);
            }
        }
    }

    Bit_OT_graph() : Crypto_graph(2, 2) {
        allocate_nodes_memory();
        init_all_nodes();
        for(int i=0;i<2;i++) {
            for(int j=0;j<2;j++) {
                int s1 = bit_ot_nodes[i][j][0].random_vars[0];
                int s2 = bit_ot_nodes[i][j][0].random_vars[1];
                for(int c=0;c<2;c++) {
                    int s = c==0 ? s1 : s2;
                    bit_ot_nodes[s1][s2][0].ngb.push_back(&bit_ot_nodes[s][c][1]);
                    bit_ot_nodes[s][c][1].ngb.push_back(&bit_ot_nodes[s1][s2][0]);
                }
            }
        }
    }
    
    void print_neighbours(DAMN_LONG a, DAMN_LONG b, DAMN_LONG side) {
    	Crypto_graph::print_neighbours(&bit_ot_nodes[a][b][side]);
    }
};

class Field_OLE_graph : public Crypto_graph {

    Crypto_node ***field_ole_nodes;

    public:

    void allocate_nodes_memory() {
        field_ole_nodes = new Crypto_node**[field_sz];
        for(DAMN_LONG i=0;i<field_sz;i++) {
            field_ole_nodes[i] = new Crypto_node*[field_sz];
            for(DAMN_LONG j=0;j<field_sz;j++) {
                field_ole_nodes[i][j] = new Crypto_node[2];
                field_ole_nodes[i][j][0].random_vars = new DAMN_LONG[num_random_vars];
                field_ole_nodes[i][j][1].random_vars = new DAMN_LONG[num_random_vars];
            }
        }
    }

    void init_all_nodes() {
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
            	DAMN_LONG vals[2] = {i, j};
                init_single_node(&field_ole_nodes[i][j][0], vals);
                init_single_node(&field_ole_nodes[i][j][1], vals);
            }
        }
    }

    Field_OLE_graph(DAMN_LONG f_sz) : Crypto_graph(f_sz, 2) {
    	allocate_nodes_memory();
        init_all_nodes();
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                DAMN_LONG a = field_ole_nodes[i][j][0].random_vars[0];
                DAMN_LONG p = field_ole_nodes[i][j][0].random_vars[1];
                for(DAMN_LONG q=0;q<field_sz;q++) {
                    DAMN_LONG b = field_minus(field_multiply(p,q),a);
                    field_ole_nodes[a][p][0].ngb.push_back(&field_ole_nodes[b][q][1]);
                    field_ole_nodes[b][q][1].ngb.push_back(&field_ole_nodes[a][p][0]);
                }
            }
        }
    }
    
    void print_neighbours(DAMN_LONG a, DAMN_LONG b, DAMN_LONG side) {
    	Crypto_graph::print_neighbours(&field_ole_nodes[a][b][side]);
    }
};

class Beaver_Triples_graph : Crypto_graph {

    Crypto_node ****beaver_triple_nodes;

    public:

    void allocate_nodes_memory() {
        beaver_triple_nodes = new Crypto_node***[field_sz];
        for(DAMN_LONG i=0;i<field_sz;i++) {
            beaver_triple_nodes[i] = new Crypto_node**[field_sz];
            for(DAMN_LONG j=0;j<field_sz;j++) {
                beaver_triple_nodes[i][j] = new Crypto_node*[field_sz];
                for(DAMN_LONG k=0;k<field_sz;k++) {
                    beaver_triple_nodes[i][j][k] = new Crypto_node[2];
                    beaver_triple_nodes[i][j][k][0].random_vars = new DAMN_LONG[num_random_vars];
                	beaver_triple_nodes[i][j][k][1].random_vars = new DAMN_LONG[num_random_vars];
                }
            }
        }
    }

    void init_all_nodes() {
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                for(DAMN_LONG k=0;k<field_sz;k++) {
                	DAMN_LONG vals[3] = {i, j, k};
	                init_single_node(&beaver_triple_nodes[i][j][k][0], vals);
	                init_single_node(&beaver_triple_nodes[i][j][k][1], vals);
                }
            }
        }
    }

    Beaver_Triples_graph(DAMN_LONG f_sz) : Crypto_graph(f_sz, 3) {
        allocate_nodes_memory();
        init_all_nodes();
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                for(DAMN_LONG k=0;k<field_sz;k++) {
                    DAMN_LONG a1 = beaver_triple_nodes[i][j][k][0].random_vars[0];
                    DAMN_LONG b1 = beaver_triple_nodes[i][j][k][0].random_vars[1];
                    DAMN_LONG c1 = beaver_triple_nodes[i][j][k][0].random_vars[2];
                    for(DAMN_LONG b2=0;b2<field_sz;b2++) {
                        for(DAMN_LONG c2=0;c2<field_sz;c2++) {
                            DAMN_LONG a2 = field_minus(field_multiply(field_plus(b1,b2),field_plus(c1,c2)),a1);
                            beaver_triple_nodes[a1][b1][c1][0].ngb.push_back(&beaver_triple_nodes[a2][b2][c2][1]);
                            beaver_triple_nodes[a2][b2][c2][1].ngb.push_back(&beaver_triple_nodes[a1][b1][c1][0]);
                        }
                    }
                }
            }
        }
    }
    
    void print_neighbours(DAMN_LONG a, DAMN_LONG b, DAMN_LONG c, DAMN_LONG side) {
    	Crypto_graph::print_neighbours(&beaver_triple_nodes[a][b][c][side]);
    }
};
