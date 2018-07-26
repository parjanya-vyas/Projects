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

BIT_OT_NODE bit_ot_nodes[2][2][2];
FIELD_OLE_NODE ***field_ole_nodes;
DAMN_LONG field_sz;

DAMN_LONG field_minus(DAMN_LONG a, DAMN_LONG b) {
    return (a-b)%field_sz;
}

DAMN_LONG field_multiply(DAMN_LONG a, DAMN_LONG b) {
    return (a*b)%field_sz;
}

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

void create_bit_ot_graph() {
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

void print_bit_ot_neighbours(BIT_OT_NODE *node) {
    cout << "Number of neighbours: " << node->ngb.size() << endl;
    list<BIT_OT_NODE *> :: iterator it;
    for(it = node->ngb.begin(); it!=node->ngb.end(); it++)
        cout << "(" << (*it)->a << ", " << (*it)->b << ")" << endl;
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

void create_field_ole_graph() {
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

void print_field_ole_neighbours(FIELD_OLE_NODE *node) {
    cout << "Number of neighbours: " << node->ngb.size() << endl;
    list<FIELD_OLE_NODE *> :: iterator it;
    for(it = node->ngb.begin(); it!=node->ngb.end(); it++)
        cout << "(" << (*it)->a << ", " << (*it)->b << ")" << endl;
}

int main() {
    create_bit_ot_graph();
    int type;
    cout << "Enter choice (1 for OT, 2 for OLE):";
    cin >> type;
    if(type == 1) {
        int side, a, b;
        cout << "Enter <side> <val1> <val2> to get the neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b;
        while(side!=-1 && a!=-1 && b!=-1) {
            print_bit_ot_neighbours(&bit_ot_nodes[a][b][side]);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b;
        }
    } else if(type == 2) {
        cout << "Enter field size:";
        cin >> field_sz;
        create_field_ole_graph();
        DAMN_LONG side, a, b;
        cout << "Enter <side> <val1> <val2> to get the neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b;
        while(side!=-1 && a!=-1 && b!=-1) {
            print_field_ole_neighbours(&field_ole_nodes[a][b][side]);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b;
        }
    }

    return 0;
}
