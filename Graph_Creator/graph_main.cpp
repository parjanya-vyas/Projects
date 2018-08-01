#include "crypto_base_classes.h"
#include "crypto_primitives_graphs.h"

using namespace std;

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
            ot_graph.print_neighbours(a, b, side);
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
        cout << "Enter <side> <val1> <val2> to get the neighbourhood, isomorphic cayley node and cayley neighbourhood. -1 to exit." << endl;
        cin >> side;
        if(side == -1)
            return 0;
        cin >> a >> b;
        while(side!=-1 && a!=-1 && b!=-1) {
            ole_graph.print_neighbours(a, b, side);
            ole_graph.print_cayley_neighbours(a, b, side);
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
            triples_graph.print_neighbours(a, b, c, side);
            cin >> side; 
            if(side==-1)
                return 0;
            cin >> a >> b >> c;
        }
    }

    return 0;
}
