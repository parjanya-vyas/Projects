#include <bits/stdc++.h>

using namespace std;

#define INVERT(N) 1-N

typedef unsigned long long DAMN_LONG;

typedef struct bit_ot_node {
    int a,b;
    list<struct bit_ot_node *> ngb;
} BIT_OT_NODE;

typedef struct field_ole_node {
    DAMN_LONG a,b;
    list<struct field_ole_node *> ngb;
} FIELD_OLE_NODE;

typedef struct beaver_triple_node {
    DAMN_LONG a,b,c;
    list<struct beaver_triple_node *> ngb;
} BEAVER_TRIPLE_NODE;

class Bit_OT_graph {

    BIT_OT_NODE bit_ot_nodes[2][2][2];

    public:

    void init_bit_ot_node(BIT_OT_NODE *n, int a, int b) {
        n->a = a;
        n->b = b;
    }

    void init_all_bit_ot_nodes() {
        for(int i=0;i<2;i++) {
            for(int j=0;j<2;j++) {
                init_bit_ot_node(&bit_ot_nodes[i][j][0], i, j);
                init_bit_ot_node(&bit_ot_nodes[i][j][1], i, j);
            }
        }
    }

    Bit_OT_graph() {
        init_all_bit_ot_nodes();
        for(int i=0;i<2;i++) {
            for(int j=0;j<2;j++) {
                int s1 = bit_ot_nodes[i][j][0].a;
                int s2 = bit_ot_nodes[i][j][0].b;
                for(int c=0;c<2;c++) {
                    int s = c==0 ? s1 : s2;
                    bit_ot_nodes[s1][s2][0].ngb.push_back(&bit_ot_nodes[s][c][1]);
                    bit_ot_nodes[s][c][1].ngb.push_back(&bit_ot_nodes[s1][s2][0]);
                }
            }
        }
    }

    void print_bit_ot_neighbours(int a, int b, int side) {
        BIT_OT_NODE node = bit_ot_nodes[a][b][side];
        cout << "Number of neighbours: " << node.ngb.size() << endl;
        list<BIT_OT_NODE *> :: iterator it;
        for(it = node.ngb.begin(); it!=node.ngb.end(); it++)
            cout << "(" << (*it)->a << ", " << (*it)->b << ")" << endl;
    }
};

class Field_OLE_graph {

    FIELD_OLE_NODE ***field_ole_nodes;
    DAMN_LONG field_sz;

    public:

    DAMN_LONG field_minus(DAMN_LONG a, DAMN_LONG b) {
        return (a-b)%field_sz;
    }

    DAMN_LONG field_multiply(DAMN_LONG a, DAMN_LONG b) {
        return (a*b)%field_sz;
    }

    void allocate_field_ole_nodes_memory() {
        field_ole_nodes = new FIELD_OLE_NODE**[field_sz];
        for(DAMN_LONG i=0;i<field_sz;i++) {
            field_ole_nodes[i] = new FIELD_OLE_NODE*[field_sz];
            for(DAMN_LONG j=0;j<field_sz;j++)
                field_ole_nodes[i][j] = new FIELD_OLE_NODE[2];
        }
    }

    void init_field_ole_node(FIELD_OLE_NODE *n, DAMN_LONG a, DAMN_LONG b) {
        n->a = a;
        n->b = b;
    }

    void init_all_field_ole_nodes() {
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                init_field_ole_node(&field_ole_nodes[i][j][0], i, j);
                init_field_ole_node(&field_ole_nodes[i][j][1], i, j);
            }
        }
    }

    Field_OLE_graph(DAMN_LONG f_sz) {
        field_sz = f_sz;
        allocate_field_ole_nodes_memory();
        init_all_field_ole_nodes();
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                DAMN_LONG a = field_ole_nodes[i][j][0].a;
                DAMN_LONG p = field_ole_nodes[i][j][0].b;
                for(DAMN_LONG q=0;q<field_sz;q++) {
                    DAMN_LONG b = field_minus(field_multiply(p,q),a);
                    field_ole_nodes[a][p][0].ngb.push_back(&field_ole_nodes[b][q][1]);
                    field_ole_nodes[b][q][1].ngb.push_back(&field_ole_nodes[a][p][0]);
                }
            }
        }
    }

    void print_field_ole_neighbours(DAMN_LONG a, DAMN_LONG b, DAMN_LONG side) {
        FIELD_OLE_NODE node = field_ole_nodes[a][b][side];
        cout << "Number of neighbours: " << node.ngb.size() << endl;
        list<FIELD_OLE_NODE *> :: iterator it;
        for(it = node.ngb.begin(); it!=node.ngb.end(); it++)
            cout << "(" << (*it)->a << ", " << (*it)->b << ")" << endl;
    }
};

class Beaver_Triples_graph {

    BEAVER_TRIPLE_NODE ****beaver_triple_nodes;
    DAMN_LONG field_sz;

    public:

    DAMN_LONG field_minus(DAMN_LONG a, DAMN_LONG b) {
        return (a-b)%field_sz;
    }

    DAMN_LONG field_plus(DAMN_LONG a, DAMN_LONG b) {
        return (a+b)%field_sz;
    }

    DAMN_LONG field_multiply(DAMN_LONG a, DAMN_LONG b) {
        return (a*b)%field_sz;
    }

    void allocate_beaver_triple_nodes_memory() {
        beaver_triple_nodes = new BEAVER_TRIPLE_NODE***[field_sz];
        for(DAMN_LONG i=0;i<field_sz;i++) {
            beaver_triple_nodes[i] = new BEAVER_TRIPLE_NODE**[field_sz];
            for(DAMN_LONG j=0;j<field_sz;j++) {
                beaver_triple_nodes[i][j] = new BEAVER_TRIPLE_NODE*[field_sz];
                for(DAMN_LONG k=0;k<field_sz;k++)
                    beaver_triple_nodes[i][j][k] = new BEAVER_TRIPLE_NODE[2];
            }
        }
    }

    void init_beaver_triple_node(BEAVER_TRIPLE_NODE *n, DAMN_LONG a, DAMN_LONG b, DAMN_LONG c) {
        n->a = a;
        n->b = b;
        n->c = c;
    }

    void init_all_beaver_triple_nodes() {
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                for(DAMN_LONG k=0;k<field_sz;k++) {
                    init_beaver_triple_node(&beaver_triple_nodes[i][j][k][0], i, j, k);
                    init_beaver_triple_node(&beaver_triple_nodes[i][j][k][1], i, j, k);
                }
            }
        }
    }

    Beaver_Triples_graph(DAMN_LONG f_sz) {
        field_sz = f_sz;
        allocate_beaver_triple_nodes_memory();
        init_all_beaver_triple_nodes();
        for(DAMN_LONG i=0;i<field_sz;i++) {
            for(DAMN_LONG j=0;j<field_sz;j++) {
                for(DAMN_LONG k=0;k<field_sz;k++) {
                    DAMN_LONG a1 = beaver_triple_nodes[i][j][k][0].a;
                    DAMN_LONG b1 = beaver_triple_nodes[i][j][k][0].b;
                    DAMN_LONG c1 = beaver_triple_nodes[i][j][k][0].c;
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

    void print_beaver_triple_neighbours(DAMN_LONG a, DAMN_LONG b, DAMN_LONG c, DAMN_LONG side) {
        BEAVER_TRIPLE_NODE node = beaver_triple_nodes[a][b][c][side];
        cout << "Number of neighbours: " << node.ngb.size() << endl;
        list<BEAVER_TRIPLE_NODE *> :: iterator it;
        for(it = node.ngb.begin(); it!=node.ngb.end(); it++)
            cout << "(" << (*it)->a << ", " << (*it)->b << ", " << (*it)->c << ")" << endl;
    }
};

int main() {
    int type;
    cout << "Enter choice (1 for OT, 2 for OLE, 3 for Beaver Triples):";
    cin >> type;
    if(type == 1) {
        Bit_OT_graph ot_graph;
        int side, a, b;
        cout << "Enter <side> <val1> <val2> to get the neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b;
        while(side!=-1 && a!=-1 && b!=-1) {
            ot_graph.print_bit_ot_neighbours(a, b, side);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b;
        }
    } else if(type == 2) {
        DAMN_LONG field_sz;
        cout << "Enter field size:";
        cin >> field_sz;
        Field_OLE_graph ole_graph(field_sz);
        DAMN_LONG side, a, b;
        cout << "Enter <side> <val1> <val2> to get the neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b;
        while(side!=-1 && a!=-1 && b!=-1) {
            ole_graph.print_field_ole_neighbours(a, b, side);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b;
        }
    } else if(type == 3) {
        DAMN_LONG field_sz;
        cout << "Enter field size:";
        cin >> field_sz;
        Beaver_Triples_graph triples_graph(field_sz);
        DAMN_LONG side, a, b, c;
        cout << "Enter <side> <val1> <val2> <val3> to get the neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b >> c;
        while(side!=-1 && a!=-1 && b!=-1 && c!=-1) {
            triples_graph.print_beaver_triple_neighbours(a, b, c, side);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b >> c;
        }
    }

    return 0;
}
